package com.sadlier.sadliermq.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ExceptionHandler;
import com.sadlier.sadliermq.annotation.EnableRabbitRetryAndDlq;
import com.sadlier.sadliermq.common.properties.SadlierMQProperties;
import com.sadlier.sadliermq.config.SadlierMQFactory;
import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import com.sadlier.sadliermq.service.QueueRetryComponent;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Service
@Log4j2
public class ServiceBReceiver {
    @Autowired
    SadlierRabbitTemplate sadlierRabbitTemplate;

    @Autowired
    SadlierMQFactory sadlierMQDeclare;

    @Value("${rmp.retry.max.count}")
    private Integer MAX_RETRY_COUNT;

    @Autowired
    RabbitListenerEndpointRegistry registry;

    @Autowired
    QueueRetryComponent queueRetryComponent;

    @RabbitListener(id = "userQ", containerFactory = "rabbitListenerContainerFactoryCustom", queues = "#{autoDeclareUserQueue.name}")
    @EnableRabbitRetryAndDlq(retryWhen = {RuntimeException.class}, discardWhen = {NullPointerException.class})
    public void processMessage(Message message) throws InterruptedException {
        log.info("Received message: \n{}", message);

        MessageProperties properties = message.getMessageProperties();

        Map<String, Object> header = properties.getHeaders();
        String type = (String) header.get("message-type");

        handlePayload(message, type);

    }

    @RabbitListener(id = "reRetryUserQ", containerFactory = "rabbitListenerContainerFactoryCustom", queues = "#{autoDeclareRetryQueue.name}")
    @EnableRabbitRetryAndDlq(retryWhen = {RuntimeException.class}, discardWhen = {NullPointerException.class})
    public void reProcessMessage(Message message) throws InterruptedException {
        log.info("Received message: \n{}", message);

        MessageProperties properties = message.getMessageProperties();

        Map<String, Object> header = properties.getHeaders();
        String type = (String) header.get("message-type");

        handlePayload(message, type);

    }

    private void handlePayload(Message message, String type) throws InterruptedException {
        if (type.equalsIgnoreCase("good-message")) {
            // simulate successful processing
            Thread.sleep(2000);
        } else if(type.equalsIgnoreCase("bad-message")) {
            // should be retried
            Thread.sleep(2000);
            throw new RuntimeException("Failed to process. Will be retried");
        } else{
            // routing to error exchange
            Thread.sleep(2000);
            throw new NullPointerException("Failed to process. Will direct to error exchange");
        }
    }

//    public void registerCustomQtoListener() throws InterruptedException {
//        AbstractMessageListenerContainer listenerContainer = (AbstractMessageListenerContainer) registry.getListenerContainer("userQ");
//
//        // for (MessageListenerContainer listenerContainer :messageListenerContainers) {
//        if (!(listenerContainer.getQueueNames().length > 0))
//            listenerContainer.addQueueNames(sadlierMQDeclare.getUserQ());
//        System.out.println(listenerContainer);
//        if (!listenerContainer.isRunning())
//            listenerContainer.start();
//        Thread.sleep(100);
//
//    }
}
