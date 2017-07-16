package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.queue.PublishMessageToQueue;
import com.intrbiz.hcq.model.message.queue.PublishedMessageToQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolError;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class PublishMessageToQueueHandler implements ProtocolHandler<PublishMessageToQueue, PublishedMessageToQueue>
{
    private static Logger logger = Logger.getLogger(PublishMessageToQueueHandler.class);
    
    @Override
    public Class<PublishMessageToQueue> getRequestType()
    {
        return PublishMessageToQueue.class;
    }

    @Override
    public PublishedMessageToQueue process(HCQServerHandler context, PublishMessageToQueue pmtq) throws Exception
    {
        HCQQueue queue = HCQBroker.get().getQueue(pmtq.getQueueName());
        if (queue == null) throw new ProtocolError("Queue '" + pmtq.getQueueName() + "' does not exist");
        // publish to the queue
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Publising message to queue " + queue.getInfo().getName());
        queue.put(pmtq.getMessage());
        return new PublishedMessageToQueue(pmtq);
    }
}
