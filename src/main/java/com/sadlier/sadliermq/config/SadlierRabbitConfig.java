package com.sadlier.sadliermq.config;

import com.sadlier.sadliermq.service.SadlierRabbitAdmin;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sang.le-hoang
 * @created Oct 13, 2021
 */
@Configuration
@Log4j2
public class SadlierRabbitConfig {
    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPwd;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${rmq.custom.consumer.count}")
    private String rmqCustomQconsumerCount;

    @Bean
    public ConnectionFactory connectionFactory() {
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPwd);
        connectionFactory.setConnectionTimeout(0);

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());
        return admin;
    }

    @Bean
    public SadlierRabbitAdmin sadlierRabbitAdmin(){
        RabbitAdmin rabbitAdmin = rabbitAdmin();
        rabbitAdmin.setIgnoreDeclarationExceptions(false);

        return new SadlierRabbitAdmin(rabbitAdmin);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryCustom() {
        SimpleRabbitListenerContainerFactory listenerContainerFcatory = new SimpleRabbitListenerContainerFactory();

        listenerContainerFcatory.setConnectionFactory(connectionFactory());
        String s= this.rmqCustomQconsumerCount;
        int ccount=0;
        if (null != s)
            ccount= Integer.valueOf(s);
        listenerContainerFcatory.setConcurrentConsumers(ccount);
        listenerContainerFcatory.setMaxConcurrentConsumers(10);
        listenerContainerFcatory.setPrefetchCount(4);
        listenerContainerFcatory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return listenerContainerFcatory;
    }
}
