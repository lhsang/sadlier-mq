package com.sadlier.sadliermq.publisher;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
public class SadlierRabbitTemplate extends RabbitTemplate {
    public SadlierRabbitTemplate(ConnectionFactory connectionFactory) {
        super.setConnectionFactory(connectionFactory);
    }
}
