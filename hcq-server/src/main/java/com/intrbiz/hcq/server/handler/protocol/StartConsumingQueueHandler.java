package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.queue.StartConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StartedConsumingQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolError;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class StartConsumingQueueHandler implements ProtocolHandler<StartConsumingQueue, StartedConsumingQueue>
{
    private static Logger logger = Logger.getLogger(StartConsumingQueueHandler.class);
    
    @Override
    public Class<StartConsumingQueue> getRequestType()
    {
        return StartConsumingQueue.class;
    }

    @Override
    public StartedConsumingQueue process(HCQServerHandler context, StartConsumingQueue cq) throws Exception
    {
        if (context.getConsuming().containsKey(cq.getQueueName()))
            return new StartedConsumingQueue(cq);
        // get the queue
        HCQQueue queue = HCQBroker.get().getQueue(cq.getQueueName());
        if (queue == null) throw new ProtocolError("Queue '" + cq.getQueueName() + "' does not exist");
        // setup the consume task
        context.getConsuming().put(queue.getInfo().getName(), queue);
        context.getDispatcher().startConsuming(queue, context);
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] consuming queue " + cq.getQueueName());
        // all setup
        return new StartedConsumingQueue(cq);
    }
}
