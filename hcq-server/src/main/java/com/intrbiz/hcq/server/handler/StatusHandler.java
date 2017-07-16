package com.intrbiz.hcq.server.handler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

public interface StatusHandler
{
    public String getPath();
    
    DefaultFullHttpResponse process(FullHttpRequest request) throws Exception;
}
