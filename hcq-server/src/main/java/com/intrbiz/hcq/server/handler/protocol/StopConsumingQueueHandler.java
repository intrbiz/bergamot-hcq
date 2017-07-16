package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.queue.StopConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StoppedConsumingQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolError;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class StopConsumingQueueHandler implements ProtocolHandler<StopConsumingQueue, StoppedConsumingQueue>
{
    private static Logger logger = Logger.getLogger(StopConsumingQueueHandler.class);
    
    @Override
    public Class<StopConsumingQueue> getRequestType()
    {
        return StopConsumingQueue.class;
    }

    @Override
    public StoppedConsumingQueue process(HCQServerHandler context, StopConsumingQueue cq) throws Exception
    {
        // get the queue
        HCQQueue queue = HCQBroker.get().getQueue(cq.getQueueName());
        if (queue == null) throw new ProtocolError("Queue '" + cq.getQueueName() + "' does not exist");
        // setup the consume task
        context.getDispatcher().stopConsuming(queue, context);
        context.getConsuming().remove(queue.getInfo().getName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] stopped consuming queue " + cq.getQueueName());
        // all setup
        return new StoppedConsumingQueue(cq);
    }
}
