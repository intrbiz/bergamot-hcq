package com.intrbiz.hcq.broker;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.IQueue;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.QueueInfo;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

public class QueueImpl implements HCQQueue
{
    private final QueueInfo info;
    
    private final IQueue<QueuedMessage> queue;
    
    public QueueImpl(QueueInfo info, IQueue<QueuedMessage> queue)
    {
        this.info = info;
        this.queue = queue;
    }

    @Override
    public QueuedMessage take() throws InterruptedException
    {
        return this.queue.take();
    }

    @Override
    public boolean offer(QueuedMessage message)
    {
        return this.queue.offer(message);
    }

    @Override
    public QueueInfo getInfo()
    {
        return this.info;
    }

    @Override
    public QueuedMessage poll()
    {
        return this.queue.poll();
    }

    @Override
    public QueuedMessage poll(long timeout, TimeUnit unit) throws InterruptedException
    {
        return this.queue.poll(timeout, unit);
    }

    @Override
    public boolean offer(QueuedMessage message, long timeout, TimeUnit unit) throws InterruptedException
    {
        return this.queue.offer(message, timeout, unit);
    }

    @Override
    public boolean put(QueuedMessage message) throws InterruptedException
    {
        this.queue.put(message);
        return true;
    }
}
