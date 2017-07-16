package com.intrbiz.hcq.client;

import java.util.function.Consumer;

import com.intrbiz.hcq.model.message.batch.BatchComplete;
import com.intrbiz.hcq.model.message.batch.BatchRequest;
import com.intrbiz.hcq.model.message.error.HCQError;
import com.intrbiz.hcq.model.message.exchange.BindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyExchange;
import com.intrbiz.hcq.model.message.exchange.GetOrCreateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindQueueToExchange;
import com.intrbiz.hcq.model.message.queue.DestroyQueue;
import com.intrbiz.hcq.model.message.queue.GetOrCreateQueue;
import com.intrbiz.util.HCQClientFuture;

public abstract class HCQBatch
{
    private BatchRequest request = new BatchRequest();
    
    public HCQBatch()
    {
        super();
    }
    
    // queue
    
    public HCQBatch getOrCreateQueue(String queueName, int capacity, boolean autoDelete)
    {
        this.request.add(new GetOrCreateQueue(queueName, capacity, autoDelete));
        return this;
    }
    
    public HCQBatch getOrCreateTempQueue(String queueName, int capacity)
    {
        this.request.add(new GetOrCreateQueue(queueName, capacity, true, true));
        return this;
    }
    
    public HCQBatch destroyQueue(String queueName)
    {
        this.request.add(new DestroyQueue(queueName));
        return this;
    }
    
    // exchange
    
    public HCQBatch getOrCreateExchange(String exchangeName, String exchangeType)
    {
        this.request.add(new GetOrCreateExchange(exchangeName, exchangeType));
        return this;
    }
    
    public HCQBatch destroyExchange(String exchangeName)
    {
        this.request.add(new DestroyExchange(exchangeName));
        return this;
    }
    
    public HCQBatch bindQueueToExchange(String exchangeName, String key, String targetName)
    {
        this.request.add(new BindQueueToExchange(exchangeName, key, targetName));
        return this;
    }
    
    public HCQBatch bindExchangeToExchange(String exchangeName, String key, String targetName)
    {
        this.request.add(new BindExchangeToExchange(exchangeName, key, targetName));
        return this;
    }
    
    public HCQBatch unbindQueueToExchange(String exchangeName, String key, String targetName)
    {
        this.request.add(new UnbindQueueToExchange(exchangeName, key, targetName));
        return this;
    }
    
    public HCQBatch unbindExchangeToExchange(String exchangeName, String key, String targetName)
    {
        this.request.add(new UnbindExchangeToExchange(exchangeName, key, targetName));
        return this;
    }
    
    public HCQBatch bindAlternateExchange(String exchangeName, String targetName)
    {
        this.request.add(new BindAlternateExchange(exchangeName, targetName));
        return this;
    }
    
    public HCQBatch unbindAlternateExchange(String exchangeName)
    {
        this.request.add(new UnbindAlternateExchange(exchangeName));
        return this;
    }
    
    // execute
    
    public HCQClientFuture<BatchComplete> submit()
    {
        return this.submit(this.request);
    }
    
    public void submit(Consumer<BatchComplete> onResponse, Consumer<HCQError> onError)
    {
        this.submit(this.request, onResponse, onError);
    }
    
    protected abstract HCQClientFuture<BatchComplete> submit(BatchRequest request);
    
    protected abstract void submit(BatchRequest request, Consumer<BatchComplete> onResponse, Consumer<HCQError> onError);
}
