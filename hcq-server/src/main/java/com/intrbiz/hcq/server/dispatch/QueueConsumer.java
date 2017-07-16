package com.intrbiz.hcq.server.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.queue.ReceiveMessageFromQueue;
import com.intrbiz.hcq.model.message.type.QueuedMessage;
import com.intrbiz.hcq.server.HCQServerHandler;

public class QueueConsumer implements Runnable
{
    private volatile boolean run = true;
    
    private final HCQQueue queue;
    
    private final List<HCQServerHandler> clients = new ArrayList<HCQServerHandler>();
    
    private int dispatchIndex = 0;
    
    public QueueConsumer(HCQQueue queue)
    {
        this.queue = queue;
    }
    
    public void run()
    {
        while (this.run)
        {
            try
            {
                // get a message
                QueuedMessage message = queue.poll(5, TimeUnit.SECONDS);
                if (message != null)
                {
                    // dispatch the message
                    HCQServerHandler client = this.pickClient();
                    if (client != null)
                    {
                        client.sendMessageToClient(new ReceiveMessageFromQueue(queue.getInfo().getName(), message));
                    }
                    else
                    {
                        queue.put(message);
                    }
                }
            }
            catch (InterruptedException e)
            {
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private HCQServerHandler pickClient()
    {
        synchronized (this.clients)
        {
            int size = this.clients.size();
            if (size == 0) return null;
            this.dispatchIndex = (this.dispatchIndex + 1) % size;
            System.out.println("Dispatch index: " + this.dispatchIndex);
            return this.clients.get(this.dispatchIndex);
        }
    }
    
    public int addClient(HCQServerHandler client)
    {
        synchronized (this.clients)
        {
            if (! clients.contains(client))
            {
                clients.add(client);
            }
            return clients.size();
        }
    }
    
    public int removeClient(HCQServerHandler client)
    {
        synchronized (this.clients)
        {
            this.clients.remove(client);
            return this.clients.size();
        }
    }
    
    public void shutdown()
    {
        this.run = false;
    }
}
