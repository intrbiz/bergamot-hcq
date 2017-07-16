package com.intrbiz.hcq.server.handler;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.server.handler.status.ClientsHandler;
import com.intrbiz.hcq.server.handler.status.ExchangesHandler;
import com.intrbiz.hcq.server.handler.status.HealthCheckStatusHandler;
import com.intrbiz.hcq.server.handler.status.InfoStatusHandler;
import com.intrbiz.hcq.server.handler.status.QueuesHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class StatusProcessor
{
    private static final Logger logger = Logger.getLogger(StatusProcessor.class);
    
    private Map<String, StatusHandler> handlers = new HashMap<String, StatusHandler>();
    
    public StatusProcessor()
    {
        super();
        // register default handlers
        this.registerHandler(new HealthCheckStatusHandler());
        this.registerHandler(new InfoStatusHandler());
        this.registerHandler(new ExchangesHandler());
        this.registerHandler(new ClientsHandler());
        this.registerHandler(new QueuesHandler());
    }
    
    public void registerHandler(StatusHandler handler)
    {
        this.handlers.put(handler.getPath(), handler);
    }
    
    public DefaultFullHttpResponse process(String path, FullHttpRequest request)
    {
        try
        {
            StatusHandler handler = this.handlers.get(path);
            if (handler != null)
            {
                return handler.process(request);
            }
            else
            {
                return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8));    
            }
        }
        catch (Exception e)
        {
            logger.error("Error processing status request", e);
            return new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer("Error processing request", CharsetUtil.UTF_8));
        }
    }
}
