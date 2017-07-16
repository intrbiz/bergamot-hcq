package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.UnbindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundExchangeToExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class UnbindExchangeToExchangeHandler implements ProtocolHandler<UnbindExchangeToExchange, UnboundExchangeToExchange>
{
    private Logger logger = Logger.getLogger(UnbindExchangeToExchangeHandler.class);

    @Override
    public Class<UnbindExchangeToExchange> getRequestType()
    {
        return UnbindExchangeToExchange.class;
    }

    @Override
    public UnboundExchangeToExchange process(HCQServerHandler context, UnbindExchangeToExchange uee) throws Exception
    {
        HCQBroker.get().unbindExchangeToExchange(uee.getExchangeName(), uee.getKey(), uee.getTagetName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Unbound exchange " + uee.getTagetName() + " to exchange " + uee.getExchangeName() + " with key " + uee.getKey());
        return new UnboundExchangeToExchange(uee);
    }
}
