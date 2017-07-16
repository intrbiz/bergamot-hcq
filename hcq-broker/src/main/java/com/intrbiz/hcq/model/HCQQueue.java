package com.intrbiz.hcq.model;

import java.util.concurrent.TimeUnit;

import com.intrbiz.hcq.model.message.type.QueuedMessage;

public interface HCQQueue
{
    QueueInfo getInfo();
    
    QueuedMessage take() throws InterruptedException;
    
    QueuedMessage poll();
    
    QueuedMessage poll(long timeout, TimeUnit unit) throws InterruptedException;
    
    boolean offer(QueuedMessage message);
    
    boolean offer(QueuedMessage message, long timeout, TimeUnit unit) throws InterruptedException;
    
    boolean put(QueuedMessage message) throws InterruptedException;
}
