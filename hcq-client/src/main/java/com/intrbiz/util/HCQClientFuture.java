package com.intrbiz.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.intrbiz.hcq.client.HCQClientError;
import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.model.message.error.HCQError;

public class HCQClientFuture<V> implements Future<V>, Consumer<HCQResponse>
{
    private final CountDownLatch latch = new CountDownLatch(1);
    
    private volatile V value;
    
    private volatile Exception error;
    
    public HCQClientFuture()
    {
        super();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void accept(HCQResponse r)
    {
        if (r instanceof HCQError)
        {
            this.failed(new HCQClientError((HCQError) r));
        }
        else 
        {
            this.complete((V) r);
        }
    }

    public void complete(V value)
    {
        this.value = value;
        this.latch.countDown();
    }
    
    public void failed(Exception t)
    {
        this.error = t;
        this.latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public boolean isDone()
    {
        return this.latch.getCount() == 0;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException
    {
        this.latch.await();
        if (this.error != null) throw new ExecutionException(this.error);
        return this.value;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        this.latch.await(timeout, unit);
        if (this.error != null) throw new ExecutionException(this.error);
        return this.value;
    }

    public V sync() throws Exception
    {
        this.latch.await();
        if (this.error != null) 
            throw this.error;
        return this.value;
    }
}
