package com.sadlier.sadliermq.config;

import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Enumeration;
import java.util.Properties;

/**
 * @author sang.le-hoang
 * @created Oct 13, 2021
 */
@Configuration
@Log4j2
public class SadlierRabbitTemplateConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SadlierRabbitTemplate sadlierRabbitTemplate(ConnectionFactory connectionFactory){
        final SadlierRabbitTemplate sadlierRabbitTemplate = new SadlierRabbitTemplate(connectionFactory);
        sadlierRabbitTemplate.setMessageConverter(jsonMessageConverter());
        printConnectionInfo(sadlierRabbitTemplate);
        return sadlierRabbitTemplate;
    }

    private void printConnectionInfo(SadlierRabbitTemplate sadlierRabbitTemplate) {
        CachingConnectionFactory connectionFactory2 = (CachingConnectionFactory) sadlierRabbitTemplate.getConnectionFactory();
        System.out.println("cachemode:" + connectionFactory2.getCacheMode());
        System.out.println("Default close time out:" + CachingConnectionFactory.DEFAULT_CLOSE_TIMEOUT
                + "channel cache size:" + connectionFactory2.getChannelCacheSize());
        Properties p = connectionFactory2.getCacheProperties();
        Enumeration<?> keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = (String) p.get(key);
            System.out.println(key + ": " + value);
        }
    }
}
