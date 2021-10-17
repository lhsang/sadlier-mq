package com.sadlier.sadliermq.common.properties;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
public class SadlierMQProperties {
    private boolean clusterMode = false;

    private Integer ttlRetryMessage = 1;

    private Integer maxRetriesAttempts = 3;

    private Integer ttlMultiply = 5;

    private String queueRetry;
    private String queueDlq;
    private String queueWait;
    private String queue;

    private String exchangeRetry;
    private String exchangeDlq;
    private String exchangeWait;
    private String exchange;

    private boolean automaticRecovery;

    public boolean isClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public Integer getTtlRetryMessage() {
        return ttlRetryMessage;
    }

    public void setTtlRetryMessage(Integer ttlRetryMessage) {
        this.ttlRetryMessage = ttlRetryMessage;
    }

    public Integer getMaxRetriesAttempts() {
        return maxRetriesAttempts;
    }

    public void setMaxRetriesAttempts(Integer maxRetriesAttempts) {
        this.maxRetriesAttempts = maxRetriesAttempts;
    }

    public Integer getTtlMultiply() {
        return ttlMultiply;
    }

    public void setTtlMultiply(Integer ttlMultiply) {
        this.ttlMultiply = ttlMultiply;
    }

    public String getQueueRetry() {
        return queueRetry;
    }

    public void setQueueRetry(String queueRetry) {
        this.queueRetry = queueRetry;
    }

    public String getQueueDlq() {
        return queueDlq;
    }

    public void setQueueDlq(String queueDlq) {
        this.queueDlq = queueDlq;
    }

    public boolean isAutomaticRecovery() {
        return automaticRecovery;
    }

    public void setAutomaticRecovery(boolean automaticRecovery) {
        this.automaticRecovery = automaticRecovery;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueueWait() {
        return queueWait;
    }

    public void setQueueWait(String queueWait) {
        this.queueWait = queueWait;
    }

    public String getExchangeRetry() {
        return exchangeRetry;
    }

    public void setExchangeRetry(String exchangeRetry) {
        this.exchangeRetry = exchangeRetry;
    }

    public String getExchangeDlq() {
        return exchangeDlq;
    }

    public void setExchangeDlq(String exchangeDlq) {
        this.exchangeDlq = exchangeDlq;
    }

    public String getExchangeWait() {
        return exchangeWait;
    }

    public void setExchangeWait(String exchangeWait) {
        this.exchangeWait = exchangeWait;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
