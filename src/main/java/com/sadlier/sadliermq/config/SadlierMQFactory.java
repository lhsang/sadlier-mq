package com.sadlier.sadliermq.config;

import com.sadlier.sadliermq.common.properties.SadlierMQProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Configuration
@Log4j2
public class SadlierMQFactory {
    public static final String EXCHANGE_USER = "sadlier.user.exchange";
    public static final String EXCHANGE_RETRY = "sadlier.retry.exchange";
    public static final String EXCHANGE_DQL = "sadlier.dql.exchange";
    public static final String EXCHANGE_WAIT = "sadlier.wait.exchange";

    public static final String RTK_TOPIC = "user*";
    public static final String RTK_RETRY_TOPIC = "retry*";
    public static final String RTK_DLQ_TOPIC ="dql";
    public static final String RTK_WAIT_TOPIC="wait";

    @Value("${rmq.user.queue.name}")
    private String userQ;
    @Value("${rmq.retry.queue.name}")
    private String retryQ;
    @Value("${rmq.dlq.queue.name}")
    private String dlq;
    @Value("${rmq.wait.queue.name}")
    private String waitQ;

    public String getUserQ() {
        return userQ;
    }

    public String getRetryQ() {
        return retryQ;
    }

    public String getDlq() {
        return dlq;
    }

    public String getWaitQ() {
        return waitQ;
    }

    @Bean
    @Primary
    SadlierMQProperties sadlierMQProperties(){
        SadlierMQProperties properties = new SadlierMQProperties();
        properties.setExchange(EXCHANGE_USER);
        properties.setExchangeRetry(EXCHANGE_RETRY);
        properties.setExchangeDlq(EXCHANGE_DQL);
        properties.setExchangeWait(EXCHANGE_WAIT);

        properties.setQueue(userQ);
        properties.setQueueDlq(dlq);
        properties.setQueueRetry(retryQ);
        properties.setQueueWait(waitQ);

        return properties;
    }

    //################################# CREATE QUEUE START ##############################
    @Bean
    Queue autoDeclareUserQueue() {
        log.info("Auto declare queue: {}", userQ);
        return QueueBuilder.durable(userQ).build();
    }

    @Bean
    Queue autoDeclareRetryQueue() {
        log.info("Auto declare queue: {}", retryQ);
        return QueueBuilder.durable(retryQ).build();
    }

    @Bean
    Queue autoDeclareDlqQueue() {
        log.info("Auto declare queue: {}", dlq);
        return QueueBuilder.durable(dlq).build();
    }

    @Bean
    Queue autoDeclareWaitQueue() {
        log.info("Auto declare queue: {}", waitQ);
        return QueueBuilder.durable(waitQ)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RETRY)
                .withArgument("x-dead-letter-routing-key", RTK_RETRY_TOPIC)
                .build();
    }

    //################################# CREATE QUEUE END ##############################

    //################################# CREATE EXCHANGE START ##############################
    @Bean
    TopicExchange autoDeclareUserExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_USER);
        return ExchangeBuilder.topicExchange(EXCHANGE_USER).build();
    }

    @Bean
    FanoutExchange autoDeclareRetryExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_RETRY);
        return ExchangeBuilder.fanoutExchange(EXCHANGE_RETRY).build();
    }

    @Bean
    FanoutExchange autoDlqExchange(SadlierMQProperties sadlierMQProperties) {
        log.info("Auto declare exchange: {}", EXCHANGE_DQL);
        return ExchangeBuilder.fanoutExchange(EXCHANGE_DQL).build();
    }

    @Bean
    FanoutExchange autoWaitExchange() {
        log.info("Auto declare exchange: {}", EXCHANGE_WAIT);
        return ExchangeBuilder.fanoutExchange(EXCHANGE_WAIT).build();
    }

    //################################# CREATE EXCHANGE END ##############################

    //################################# BINDING START ##############################
    @Bean
    Binding binding(Queue autoDeclareUserQueue, TopicExchange autoDeclareUserExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareUserQueue.getName(), autoDeclareUserExchange.getName(), RTK_TOPIC);
        return BindingBuilder.bind(autoDeclareUserQueue).to(autoDeclareUserExchange).with(RTK_TOPIC);
    }
    @Bean
    Binding bindingretry(Queue autoDeclareRetryQueue, FanoutExchange autoDeclareRetryExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareRetryQueue.getName(), autoDeclareRetryExchange.getName(), RTK_RETRY_TOPIC);
        return BindingBuilder.bind(autoDeclareRetryQueue).to(autoDeclareRetryExchange);
    }

    @Bean
    Binding bindingdlq(Queue autoDeclareDlqQueue, FanoutExchange autoDlqExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareDlqQueue.getName(), autoDlqExchange.getName(), RTK_DLQ_TOPIC);
        return BindingBuilder.bind(autoDeclareDlqQueue).to(autoDlqExchange);
    }

    @Bean
    Binding bindingwait(Queue autoDeclareWaitQueue, FanoutExchange autoWaitExchange) {
        log.info("Auto bind queue {} to exchange: {} with routing key {}", autoDeclareWaitQueue.getName(), autoWaitExchange.getName(), RTK_WAIT_TOPIC);
        return BindingBuilder.bind(autoDeclareWaitQueue).to(autoWaitExchange);
    }
    //################################# BINDING END ##############################
}
