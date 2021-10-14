package com.sadlier.sadliermq.consumer;

import com.rabbitmq.client.Channel;
import com.sadlier.sadliermq.config.SadlierMQDeclare;
import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
    SadlierMQDeclare sadlierMQDeclare;

    @Value("${rmp.retry.max.count}")
    private Integer MAX_RETRY_COUNT;

    @RabbitListener(id = "customq", autoStartup = "true", containerFactory = "rabbitListenerContainerFactoryCustom")
    public void processMessage(Message message, Channel channel) {
        long deliverytag = 0;
        try {
            log.info("Received message: \n{}", message);
            int num = channel.getChannelNumber();

            MessageProperties properties = message.getMessageProperties();
            deliverytag = properties.getDeliveryTag();
            String messageId = properties.getMessageId();

            Map<String, Object> header = properties.getHeaders();
            String type = (String) header.get("message-type");

            log.info("Processing message #id:{}", messageId);
            handlePayload(message, type);

            channel.basicAck(deliverytag, false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendNack(message, channel, deliverytag);
        }

    }

    private void handlePayload(Message message, String type) throws InterruptedException {
        if (type.equalsIgnoreCase("good-message")) {
            // simulate succesful processing
            Thread.sleep(2000);
        } else {
            // simulate fail processing
            Thread.sleep(2000);
            throw new RuntimeException("Failed to process");
        }
    }

    private void sendNack(Message message, Channel channel, long deliverytag) {
        try {
            boolean ismaxTryreached = false;
            MessageProperties props = message.getMessageProperties();
            List<Map<String, ?>> headers = props.getXDeathHeader();// getHeaders();

            if (headers != null)
                for (Map<String, ?> m : headers) {
                    System.out.println(m);
                    if (((String) m.get("queue")).equalsIgnoreCase(sadlierMQDeclare.getUserQ())) {
                        long c = ((Long) m.get("count")).longValue();

                        System.out.println("count is:" + c + " queue is:" + m.get("queue"));
                        if (c == MAX_RETRY_COUNT)
                            ismaxTryreached = true;
                    }
                }

            if (ismaxTryreached) {
                channel.basicAck(deliverytag, false);
                CorrelationData correlationData = new CorrelationData("error");
                sadlierRabbitTemplate.send(SadlierMQDeclare.EXCHANGE_ERROR, SadlierMQDeclare.RTK_ERROR_TOPIC, message,
                        correlationData);
            } else
                channel.basicNack(deliverytag, false, false);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
