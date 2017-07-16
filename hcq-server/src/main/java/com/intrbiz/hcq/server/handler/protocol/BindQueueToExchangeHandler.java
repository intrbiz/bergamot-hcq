package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.BindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundQueueToExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class BindQueueToExchangeHandler implements ProtocolHandler<BindQueueToExchange, BoundQueueToExchange>
{
    private static Logger logger = Logger.getLogger(BindQueueToExchangeHandler.class);

    @Override
    public Class<BindQueueToExchange> getRequestType()
    {
        return BindQueueToExchange.class;
    }

    @Override
    public BoundQueueToExchange process(HCQServerHandler context, BindQueueToExchange bqe) throws Exception
    {
        // do we have the queue
        HCQBroker.get().bindQueueToExchange(bqe.getExchangeName(), bqe.getKey(), bqe.getTagetName());
        if (logger.isDebugEnabled()) logger.debug("Bound queue " + bqe.getTagetName() + " to exchange " + bqe.getExchangeName() + " with key " + bqe.getKey());
        return new BoundQueueToExchange(bqe);
    }
}
