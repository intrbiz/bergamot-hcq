package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQExchange;
import com.intrbiz.hcq.model.message.exchange.GetOrCreateExchange;
import com.intrbiz.hcq.model.message.exchange.GotExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class GetOrCreateExchangeHandler implements ProtocolHandler<GetOrCreateExchange, GotExchange>
{
    private static Logger logger = Logger.getLogger(GetOrCreateExchangeHandler.class);
    
    @Override
    public Class<GetOrCreateExchange> getRequestType()
    {
        return GetOrCreateExchange.class;
    }

    @Override
    public GotExchange process(HCQServerHandler context, GetOrCreateExchange gce) throws Exception
    {
        HCQExchange exchange = HCQBroker.get().getOrCreateExchange(gce.getExchangeName(), gce.getExchangeType());
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Created exchange " + exchange.getInfo().getName() + " [" + exchange.getInfo().getType() + "]");
        return new GotExchange(gce);
    }
}
