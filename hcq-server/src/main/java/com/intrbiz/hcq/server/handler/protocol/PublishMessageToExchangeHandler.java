package com.intrbiz.hcq.server.handler.protocol;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.HCQExchange;
import com.intrbiz.hcq.model.message.exchange.PublishMessageToExchange;
import com.intrbiz.hcq.model.message.exchange.PublishedMessageToExchange;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolError;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class PublishMessageToExchangeHandler implements ProtocolHandler<PublishMessageToExchange, PublishedMessageToExchange>
{
    private static Logger logger = Logger.getLogger(PublishMessageToExchangeHandler.class);
    
    @Override
    public Class<PublishMessageToExchange> getRequestType()
    {
        return PublishMessageToExchange.class;
    }

    @Override
    public PublishedMessageToExchange process(HCQServerHandler context, PublishMessageToExchange pmte) throws Exception
    {
     // get the queue
        HCQExchange exchange = HCQBroker.get().getExchange(pmte.getExchangeName());
        if (exchange == null) throw new ProtocolError("Exchange '" + pmte.getExchangeName() + "' does not exist");
        // publish to the queue
        if (logger.isDebugEnabled()) logger.debug("[" + context.getRemoteAddress() + "] Publising message to exchange " + exchange.getInfo().getName() + " with key " + pmte.getRoutingKey());
        exchange.put(pmte.getRoutingKey(), pmte.getMessage());
        // respond
        return new PublishedMessageToExchange(pmte);
    }
}
