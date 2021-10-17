package com.sadlier.sadliermq.publisher;

import com.sadlier.sadliermq.config.SadlierMQFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Service
public class ServiceASender {
    @Autowired
    SadlierRabbitTemplate sadlierRabbitTemplate;

    public String sendAGoodMsgCreateUser(Object message){
        MessageProperties messageProperties = new MessageProperties();
        Map<String, Object> header = messageProperties.getHeaders();
        header.put("message-type", "good-message");
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message rmqmessage = messageConverter.toMessage(message, messageProperties);
        sadlierRabbitTemplate.send(SadlierMQFactory.EXCHANGE_USER, SadlierMQFactory.RTK_TOPIC, rmqmessage);

        return "Sent message: "+ rmqmessage;
    }

    public String sendABadMsgCreateUser(String message){
        MessageProperties messageProperties = new MessageProperties();
        Map<String, Object> header = messageProperties.getHeaders();
        header.put("message-type", "bad-message");
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message rmqmessage = messageConverter.toMessage(message, messageProperties);
        sadlierRabbitTemplate.send(SadlierMQFactory.EXCHANGE_USER, SadlierMQFactory.RTK_TOPIC, rmqmessage);

        return "Sent message: "+ rmqmessage;
    }

    public String sendAErrMsgCreateUser(String message){
        MessageProperties messageProperties = new MessageProperties();
        Map<String, Object> header = messageProperties.getHeaders();
        header.put("message-type", "error-message");
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message rmqmessage = messageConverter.toMessage(message, messageProperties);
        sadlierRabbitTemplate.send(SadlierMQFactory.EXCHANGE_USER, SadlierMQFactory.RTK_TOPIC, rmqmessage);

        return "Sent message: "+ rmqmessage;
    }

}
