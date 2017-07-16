package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.UnbindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundQueueToExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class UnindQueueToExchangeHandler implements ProtocolHandler<UnbindQueueToExchange, UnboundQueueToExchange>
{
    private static Logger logger = Logger.getLogger(UnindQueueToExchangeHandler.class);

    @Override
    public Class<UnbindQueueToExchange> getRequestType()
    {
        return UnbindQueueToExchange.class;
    }

    @Override
    public UnboundQueueToExchange process(HCQServerHandler context, UnbindQueueToExchange uqe) throws Exception
    {
        // do we have the queue
        HCQBroker.get().bindQueueToExchange(uqe.getExchangeName(), uqe.getKey(), uqe.getTagetName());
        if (logger.isDebugEnabled()) logger.debug("Unbound queue " + uqe.getTagetName() + " from exchange " + uqe.getExchangeName() + " with key " + uqe.getKey());
        return new UnboundQueueToExchange(uqe);
    }
}
