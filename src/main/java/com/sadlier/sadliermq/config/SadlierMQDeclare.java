package com.sadlier.sadliermq.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Configuration
@Log4j2
public class SadlierMQDeclare {
    public static final String EXCHANGE_USER = "sadlier.user.exchange";
    public static final String EXCHANGE_RETRY = "sadlier.retry.exchange";
    public static final String EXCHANGE_ERROR = "sadlier.error.exchange";

    public static final String RTK_TOPIC = "user*";
    public static final String RTK_RETRY_TOPIC = "retry*";
    public static final String RTK_ERROR_TOPIC="error";

    @Value("${rmq.user.queue.name}")
    private String userQ;
    @Value("${rmq.retry.queue.name}")
    private String retryQ;
    @Value("${rmq.error.queue.name}")
    private String errorQ;

    public String getUserQ() {return userQ;}
    public String getRetryQ() {return retryQ;}
    public String getErrorQ() {return errorQ;}

    @Bean
    Queue autoDeclareUserQueue() {
        log.info("Auto declare queue: {}", userQ);
        return QueueBuilder.durable(userQ)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RETRY)
                .withArgument("x-dead-letter-routing-key", RTK_RETRY_TOPIC).build();
    }

    @Bean
    Queue autoDeclareRetryQueue() {
        log.info("Auto declare queue: {}", retryQ);
        return QueueBuilder.durable(retryQ)
                .withArgument("x-message-ttl", 4000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_USER)
                .withArgument("x-dead-letter-routing-key", RTK_TOPIC)
                .build();
    }

    @Bean
    Queue autoDeclareErrorQueue() {
        log.info("Auto declare queue: {}", errorQ);
        return QueueBuilder.durable(errorQ).build();
    }

    @Bean
    Exchange autoDeclareUserExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_USER);
        return ExchangeBuilder.topicExchange(EXCHANGE_USER).build();
    }

    @Bean
    Exchange autoDeclareRetryExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_RETRY);
        return ExchangeBuilder.topicExchange(EXCHANGE_RETRY).build();
    }

    @Bean
    Exchange autoErrorExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_ERROR);
        return ExchangeBuilder.topicExchange(EXCHANGE_ERROR).build();
    }

    @Bean
    Binding binding(Queue autoDeclareUserQueue, TopicExchange autoDeclareUserExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareUserQueue.getName(), autoDeclareUserExchange.getName(), RTK_TOPIC);
        return BindingBuilder.bind(autoDeclareUserQueue).to(autoDeclareUserExchange).with(RTK_TOPIC);
    }
    @Bean
    Binding bindingretry(Queue autoDeclareRetryQueue, TopicExchange autoDeclareRetryExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareRetryQueue.getName(), autoDeclareRetryExchange.getName(), RTK_RETRY_TOPIC);
        return BindingBuilder.bind(autoDeclareRetryQueue).to(autoDeclareRetryExchange).with(RTK_RETRY_TOPIC);
    }
    @Bean
    Binding bindingerror(Queue autoDeclareErrorQueue, TopicExchange autoErrorExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareErrorQueue.getName(), autoErrorExchange.getName(), RTK_ERROR_TOPIC);
        return BindingBuilder.bind(autoDeclareErrorQueue).to(autoErrorExchange).with(RTK_ERROR_TOPIC);
    }
}
