package com.sadlier.sadliermq.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

/**
 * @author sang.le-hoang
 * @created Oct 13, 2021
 */
@Log4j2
public class SadlierRabbitAdmin {
    private RabbitAdmin rabbitAdmin;

    public SadlierRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void declareQueue(String queueName){
        boolean durable = false;
        boolean exclusive = false;
        boolean autoDelete = true;

        QueueBuilder queueBuilder = durable? QueueBuilder.durable(queueName):QueueBuilder.nonDurable(queueName);

        if(exclusive)   queueBuilder.exclusive();

        if(autoDelete)  queueBuilder.autoDelete();

        declareQueue(queueBuilder.build());
    }

    public void declareQueue(Queue queue){
        try {
            rabbitAdmin.declareQueue(queue);
            log.info("Succeed declared queue '{}' to broker", queue.getName());
        } catch (Exception e) {
            log.error("Failed to declare queue '{}' to broker. Reason: {}", queue.getName(), e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    public void declareExchange(String name, String type){
        ExchangeBuilder exchangeBuilder = new ExchangeBuilder(name, type);
        declareExchange(exchangeBuilder.build());
    }

    public void declareExchange(Exchange exchange){
        try {
            rabbitAdmin.declareExchange(exchange);
            log.info("Succeed declared exchange '{}' type of {} to broker", exchange.getName(), exchange.getType());
        } catch (Exception e) {
            log.error("Failed to declare exchange '{}' type of {} to broker. Reason: {}", exchange.getName(), exchange, e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    public void bindQueueToExchange(String queue, String exchange, String routingKey) {
        // routing key == '' => fanout
        // routing key like pattern => topic
        // routing key is a full text => direct
        Binding binding = new Binding(queue, Binding.DestinationType.QUEUE, exchange, routingKey, null);
        try {
            rabbitAdmin.declareBinding(binding);
            log.info("Succeed bound queue {} to exchange {} with pattern {}", queue, exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to bind queue {} to exchange {} using pattern {}. Reason: {}", queue, exchange, routingKey, e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
