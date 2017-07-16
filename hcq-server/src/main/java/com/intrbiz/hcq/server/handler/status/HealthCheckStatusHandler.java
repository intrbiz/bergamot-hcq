package com.intrbiz.hcq.server.handler.status;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.server.handler.StatusHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class HealthCheckStatusHandler implements StatusHandler
{
    @Override
    public String getPath()
    {
        return "/status";
    }

    @Override
    public DefaultFullHttpResponse process(FullHttpRequest request) throws Exception
    {
        boolean ok = HCQBroker.get().hasQuorum();
        String status = ok ? "OK" : "NOT OK";
        return new DefaultFullHttpResponse(HTTP_1_1, ok ? OK : INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(status, CharsetUtil.UTF_8));
    }
}
