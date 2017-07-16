package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.BindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BoundAlternateExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class BindAlternateExchangeHandler implements ProtocolHandler<BindAlternateExchange, BoundAlternateExchange>
{
    private Logger logger = Logger.getLogger(BindAlternateExchangeHandler.class);

    @Override
    public Class<BindAlternateExchange> getRequestType()
    {
        return BindAlternateExchange.class;
    }

    @Override
    public BoundAlternateExchange process(HCQServerHandler context, BindAlternateExchange bae) throws Exception
    {
        HCQBroker.get().bindAlternateExchange(bae.getExchangeName(), bae.getTagetName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Bound exchange " + bae.getTagetName() + " as alternate for exchange " + bae.getExchangeName());
        return new BoundAlternateExchange(bae);
    }
}
