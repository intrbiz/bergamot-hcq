package com.intrbiz.hcq.server.dispatch;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.server.HCQServerHandler;

public class QueueDispatcher
{
    private ConcurrentMap<String, QueueConsumer> consumers = new ConcurrentHashMap<String, QueueConsumer>();
    
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public QueueDispatcher()
    {
        super();
    }
    
    private QueueConsumer consumer(HCQQueue queue)
    {
        return this.consumers.computeIfAbsent(queue.getInfo().getName(), (k) -> {
            System.out.println("Setting up queue consumer for: " + k);
            QueueConsumer qcon = new QueueConsumer(queue);
            executor.submit(qcon);
            return qcon;
        });
    }
    
    public void startConsuming(HCQQueue queue, HCQServerHandler client)
    {
        synchronized(this)
        {
            QueueConsumer consumer = this.consumer(queue);
            int count = consumer.addClient(client);
            System.out.println("Starting consumer, count: " + count + " on " + queue.getInfo().getName());
        }
    }
    
    public void stopConsuming(HCQQueue queue, HCQServerHandler client)
    {
        synchronized (this)
        {
            QueueConsumer consumer = this.consumer(queue);
            int count = consumer.removeClient(client);
            System.out.println("Stopped consumer, count: " + count + " on " + queue.getInfo().getName());
            if (count == 0)
            {
                System.out.println("Shutting down consumer on " + queue.getInfo().getName());
                consumer.shutdown();
                this.consumers.remove(queue.getInfo().getName());
            }
        }
    }
}
