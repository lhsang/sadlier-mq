package com.sadlier.sadliermq.common.constant;

/**
 * @author sang.le-hoang
 * @created Oct 17, 2021
 */

/**
 * Standard x-death headers
 * x-death:
 *   count: 1
 *   exchange: baeldung-messages-exchange
 *   queue: baeldung-messages-queue
 *   reason: rejected
 *   routing-keys: baeldung-messages-queue
 *   time: 1571232954
 */
public class MessageHeaders {
    public final static String X_DEATH = "x-death";
    public static class X_Death{
        public final static String COUNT = "count";
        public final static String EXCHANGE = "exchange";
        public final static String QUEUE = "queue";
        public final static String REASON = "reason";
        public final static String ROUTING_KEYS = "routing-keys";
        public final static String TIME = "time";
    }
}
