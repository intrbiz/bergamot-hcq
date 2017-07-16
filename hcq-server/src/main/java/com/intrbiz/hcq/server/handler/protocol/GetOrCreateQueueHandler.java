package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.queue.GetOrCreateQueue;
import com.intrbiz.hcq.model.message.queue.GotQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class GetOrCreateQueueHandler implements ProtocolHandler<GetOrCreateQueue, GotQueue>
{
    private static final Logger logger = Logger.getLogger(GetOrCreateQueueHandler.class);
    
    @Override
    public Class<GetOrCreateQueue> getRequestType()
    {
        return GetOrCreateQueue.class;
    }

    @Override
    public GotQueue process(HCQServerHandler context, GetOrCreateQueue gcq) throws Exception
    {
        HCQBroker hcqb = HCQBroker.get();
        HCQQueue queue = gcq.isTemporary() ? 
                hcqb.getOrCreateTempQueue(gcq.getQueueName(), gcq.getCapacity(), context.getClientInfo()) : 
                hcqb.getOrCreateQueue(gcq.getQueueName(), gcq.getCapacity(), gcq.isAutoDelete());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Created queue " + queue.getInfo().getName());
        return new GotQueue(gcq);
    }
}
