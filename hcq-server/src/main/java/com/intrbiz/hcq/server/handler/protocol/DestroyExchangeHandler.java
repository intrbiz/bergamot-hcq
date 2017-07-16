package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.message.exchange.DestroyExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyedExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class DestroyExchangeHandler implements ProtocolHandler<DestroyExchange, DestroyedExchange>
{
    private static Logger logger = Logger.getLogger(DestroyExchangeHandler.class);
    
    @Override
    public Class<DestroyExchange> getRequestType()
    {
        return DestroyExchange.class;
    }

    @Override
    public DestroyedExchange process(HCQServerHandler context, DestroyExchange de) throws Exception
    {
        HCQBroker.get().destroyExchange(de.getExchangeName());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Destoryed exchange " + de.getExchangeName());
        return new DestroyedExchange(de);
    }
}
