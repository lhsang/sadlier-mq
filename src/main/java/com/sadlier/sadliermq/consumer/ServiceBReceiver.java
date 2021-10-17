package com.sadlier.sadliermq.consumer;

import com.sadlier.sadliermq.annotation.EnableRabbitRetryAndDlq;
import com.sadlier.sadliermq.config.SadlierMQFactory;
import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import com.sadlier.sadliermq.service.QueueRetryComponent;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    RabbitListenerEndpointRegistry registry;

    @Autowired
    QueueRetryComponent queueRetryComponent;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactoryCustom", queues = "#{autoDeclareUserQueue.name}")
    @EnableRabbitRetryAndDlq(retryWhen = {RuntimeException.class}, discardWhen = {NullPointerException.class})
    public void processMessage(Message message) {
        log.info("Received message: \n{}", message);

        MessageProperties properties = message.getMessageProperties();

        Map<String, Object> header = properties.getHeaders();
        String type = (String) header.get("message-type");

        handlePayload(message, type);

    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactoryCustom", queues = "#{autoDeclareRetryQueue.name}")
    @EnableRabbitRetryAndDlq(retryWhen = {RuntimeException.class}, discardWhen = {NullPointerException.class})
    public void reProcessMessage(Message message) {
        log.info("Received message: \n{}", message);

        MessageProperties properties = message.getMessageProperties();

        Map<String, Object> header = properties.getHeaders();
        String type = (String) header.get("message-type");

        handlePayload(message, type);

    }

    private void handlePayload(Message message, String type) {
        if (type.equalsIgnoreCase("good-message")) {
            // simulate successful processing
        } else if(type.equalsIgnoreCase("bad-message")) {
            // should be retried
            throw new RuntimeException("Failed to process. Will be retried");
        } else{
            // routing to error exchange
            throw new NullPointerException("Failed to process. Will direct to error exchange");
        }
    }
}
