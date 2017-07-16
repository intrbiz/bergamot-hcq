package com.intrbiz.hcq.model;

import java.util.concurrent.TimeUnit;

import com.intrbiz.hcq.model.message.type.QueuedMessage;

public interface HCQExchange
{
    public static final class TYPE 
    {
        public static final String FANOUT = "fanout";
        
        public static final String TOPIC  = "topic";
    }
    
    ExchangeInfo getInfo();
    
    boolean offer(String routingKey, QueuedMessage message);
    
    boolean offer(String routingKey, QueuedMessage message, long timeout, TimeUnit unit) throws InterruptedException;
    
    boolean put(String routingKey, QueuedMessage message) throws InterruptedException;
    
    default boolean offer(QueuedMessage message)
    {
        return this.offer(null, message);
    }
    
    default boolean offer(QueuedMessage message, long timeout, TimeUnit unit) throws InterruptedException
    {
        return this.offer(null, message, timeout, unit);
    }
    
    default void put(QueuedMessage message) throws InterruptedException
    {
        this.put(null, message);
    }
}
