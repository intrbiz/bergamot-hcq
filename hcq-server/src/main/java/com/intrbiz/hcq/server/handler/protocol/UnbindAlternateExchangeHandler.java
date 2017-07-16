package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.UnbindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundAlternateExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class UnbindAlternateExchangeHandler implements ProtocolHandler<UnbindAlternateExchange, UnboundAlternateExchange>
{
    private Logger logger = Logger.getLogger(UnbindAlternateExchangeHandler.class);

    @Override
    public Class<UnbindAlternateExchange> getRequestType()
    {
        return UnbindAlternateExchange.class;
    }

    @Override
    public UnboundAlternateExchange process(HCQServerHandler context, UnbindAlternateExchange uae) throws Exception
    {
        HCQBroker.get().unbindAlternateExchange(uae.getExchangeName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Unbound alternate exchange from exchange " + uae.getExchangeName());
        return new UnboundAlternateExchange(uae);
    }
}
