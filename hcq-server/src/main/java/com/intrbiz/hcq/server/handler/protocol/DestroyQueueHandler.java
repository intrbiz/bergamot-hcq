package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.queue.DestroyQueue;
import com.intrbiz.hcq.model.message.queue.DestroyedQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class DestroyQueueHandler implements ProtocolHandler<DestroyQueue, DestroyedQueue>
{
    private static Logger logger = Logger.getLogger(DestroyQueueHandler.class);
    
    @Override
    public Class<DestroyQueue> getRequestType()
    {
        return DestroyQueue.class;
    }

    @Override
    public DestroyedQueue process(HCQServerHandler context, DestroyQueue de) throws Exception
    {
        HCQBroker.get().destroyQueue(de.getQueueName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Destoryed queue " + de.getQueueName());
        return new DestroyedQueue(de);
    }
}
