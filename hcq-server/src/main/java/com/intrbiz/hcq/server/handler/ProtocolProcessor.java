package com.intrbiz.hcq.server.handler;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.model.message.error.HCQGeneralError;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.protocol.BatchRequestHandler;
import com.intrbiz.hcq.server.handler.protocol.BindAlternateExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.BindExchangeToExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.BindQueueToExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.DestroyExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.GetOrCreateExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.GetOrCreateQueueHandler;
import com.intrbiz.hcq.server.handler.protocol.PublishMessageToExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.PublishMessageToQueueHandler;
import com.intrbiz.hcq.server.handler.protocol.StartConsumingQueueHandler;
import com.intrbiz.hcq.server.handler.protocol.StopConsumingQueueHandler;
import com.intrbiz.hcq.server.handler.protocol.UnbindAlternateExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.UnbindExchangeToExchangeHandler;
import com.intrbiz.hcq.server.handler.protocol.UnindQueueToExchangeHandler;

public class ProtocolProcessor
{
    private static final Logger logger = Logger.getLogger(ProtocolProcessor.class);
    
    private Map<Class<? extends HCQRequest>, ProtocolHandler<?,?>> handlers = new IdentityHashMap<Class<? extends HCQRequest>, ProtocolHandler<?,?>>();
    
    public ProtocolProcessor()
    {
        super();
        // register default handlers
        this.registerHandler(new StartConsumingQueueHandler());
        this.registerHandler(new BindExchangeToExchangeHandler());
        this.registerHandler(new BindQueueToExchangeHandler());
        this.registerHandler(new GetOrCreateExchangeHandler());
        this.registerHandler(new GetOrCreateQueueHandler());
        this.registerHandler(new PublishMessageToExchangeHandler());
        this.registerHandler(new PublishMessageToQueueHandler());
        this.registerHandler(new UnbindExchangeToExchangeHandler());
        this.registerHandler(new UnindQueueToExchangeHandler());
        this.registerHandler(new DestroyExchangeHandler());
        this.registerHandler(new StopConsumingQueueHandler());
        this.registerHandler(new BindAlternateExchangeHandler());
        this.registerHandler(new UnbindAlternateExchangeHandler());
        this.registerHandler(new BatchRequestHandler());
    }
    
    public void registerHandler(ProtocolHandler<?,?> handler)
    {
        this.handlers.put(handler.getRequestType(), handler);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HCQResponse process(HCQServerHandler context, HCQRequest request) throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("Processing: " + request);
        try
        {
            ProtocolHandler handler = (ProtocolHandler) this.handlers.get(request.getClass());
            if (handler != null)
            {
                return handler.process(context, request);
            }
            else
            {
                return new HCQGeneralError(request, "Unimplemented");    
            }
        }
        catch (Exception e)
        {
            logger.error("Error processing HCQ request", e);
            return new HCQGeneralError(request, e.getMessage());
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HCQResponse dispatch(HCQServerHandler context, HCQRequest request) throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("Dispatching: " + request);
        ProtocolHandler handler = (ProtocolHandler) this.handlers.get(request.getClass());
        if (handler != null)
        {
            return handler.process(context, request);
        }
        else
        {
            return new HCQGeneralError(request, "Unimplemented");    
        }
    }
}
