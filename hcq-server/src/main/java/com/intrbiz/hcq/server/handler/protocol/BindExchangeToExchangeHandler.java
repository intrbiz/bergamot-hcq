package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.BindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundExchangeToExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class BindExchangeToExchangeHandler implements ProtocolHandler<BindExchangeToExchange, BoundExchangeToExchange>
{
    private Logger logger = Logger.getLogger(BindExchangeToExchangeHandler.class);

    @Override
    public Class<BindExchangeToExchange> getRequestType()
    {
        return BindExchangeToExchange.class;
    }

    @Override
    public BoundExchangeToExchange process(HCQServerHandler context, BindExchangeToExchange bee) throws Exception
    {
        HCQBroker.get().bindExchangeToExchange(bee.getExchangeName(), bee.getKey(), bee.getTagetName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Bound exchange " + bee.getTagetName() + " to exchange " + bee.getExchangeName() + " with key " + bee.getKey());
        return new BoundExchangeToExchange(bee);
    }
}
