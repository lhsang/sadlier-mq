package com.sadlier.sadliermq.common.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
public class MessageUtil {
    public static String getMessageExchange(Message message) {
        MessageProperties props = message.getMessageProperties();
        return props.getReceivedExchange();
    }

    public static String getMessageRoutingKey(Message message) {
        MessageProperties props = message.getMessageProperties();

        return props.getReceivedRoutingKey();
    }
}
