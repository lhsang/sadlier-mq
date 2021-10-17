package com.sadlier.sadliermq.service;

import com.sadlier.sadliermq.common.constant.MessageHeaders;
import com.sadlier.sadliermq.common.properties.SadlierMQProperties;
import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Component
@Log4j2
public class QueueRetryComponent {
    @Autowired
    SadlierRabbitTemplate sadlierRabbitTemplate;

    public void sendToRetryOrDlq(final Message message, final SadlierMQProperties properties) {
        final Integer qtyRetry = countDeath(message);

        if (qtyRetry > properties.getMaxRetriesAttempts()) {
            log.info("Message will be sent to Dead Letter Queue....");
            sendToDlq(message, properties);
        } else {
            log.info("Message will be sent to Retry Queue....");
            sendToRetry(message, properties, qtyRetry);
        }
    }

    public void sendToRetry(final Message message, final SadlierMQProperties properties, final Integer qtdRetry) {
        Map<String, Object> xDeath = buildXDeathHeader(qtdRetry,
                qtdRetry>1?properties.getExchangeRetry():properties.getExchange(),
                qtdRetry>1?properties.getQueueRetry():properties.getQueue(),
                "rejected");
        List<Map<String, Object>> xDeathList = new ArrayList<>();
        if(message.getMessageProperties().getHeaders().containsKey(MessageHeaders.X_DEATH)){
            xDeathList = (List<Map<String, Object>>) message.getMessageProperties().getHeaders().get(MessageHeaders.X_DEATH);
        }
        xDeathList.add(0, xDeath);

        message.getMessageProperties().getHeaders().put(MessageHeaders.X_DEATH, xDeathList);
        message.getMessageProperties()
                .setExpiration(String.valueOf(calculateTtl(properties.getTtlRetryMessage(), qtdRetry, properties.getTtlMultiply())));
        sadlierRabbitTemplate.send(properties.getExchangeWait(), properties.getQueueWait(), message);
    }

    public void sendToDlq(final Message message, final SadlierMQProperties properties) {
        Map<String, Object> xDeath = buildXDeathHeader(null, properties.getExchange(), properties.getQueue(), "max-retry");
        List<Map<String, Object>> xDeathList = new ArrayList<>();
        if(message.getMessageProperties().getHeaders().containsKey(MessageHeaders.X_DEATH)){
            xDeathList = (List<Map<String, Object>>) message.getMessageProperties().getHeaders().get(MessageHeaders.X_DEATH);
        }
        xDeathList.add(0, xDeath);

//        message.getMessageProperties().getHeaders().remove(MessageHeaders.X_DEATH);
        sadlierRabbitTemplate.send(properties.getExchangeDlq(), properties.getQueueDlq(), message);
    }


    public int countDeath(final Message message) {
        int count = 0;
        final Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (headers.containsKey(MessageHeaders.X_DEATH)) {
            count = Integer.parseInt(getXDeath(headers).get(MessageHeaders.X_Death.COUNT).toString());
        }
        return ++count;
    }
    
    protected Map getXDeath(final Map<String, Object> headers) {
        final List list = (List) Collections.singletonList(headers.get(MessageHeaders.X_DEATH)).get(0);
        return (Map) list.get(0);
    }

    public int calculateTtl(Integer ttlRetry, Integer qtdRetry, Integer ttlMultiply) {
        final AtomicInteger expiration = new AtomicInteger(ttlRetry);
        if (!ttlMultiply.equals(0) && qtdRetry > 1) {
            IntStream.range(1, qtdRetry).forEach(value -> expiration.set(expiration.get() * ttlMultiply));
        }
        return expiration.get();
    }

    private Map<String, Object> buildXDeathHeader(Integer qtdRetry, String exchange, String queue, String reason){
        Map<String, Object> xDeath = new HashMap<>();
        if(qtdRetry != null){
            xDeath.put(MessageHeaders.X_Death.COUNT, qtdRetry);
        }
        if(StringUtils.hasText(exchange)){
            xDeath.put(MessageHeaders.X_Death.EXCHANGE, exchange);
        }
        if(StringUtils.hasText(queue)){
            xDeath.put(MessageHeaders.X_Death.QUEUE, queue);
        }
        if(StringUtils.hasText(reason)){
            xDeath.put(MessageHeaders.X_Death.REASON, reason);
        }
        xDeath.put(MessageHeaders.X_Death.TIME, new Timestamp(System.currentTimeMillis()));
        return xDeath;
    }
}
