package com.intrbiz.hcq.server.handler;

import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.server.HCQServerHandler;

public interface ProtocolHandler<I extends HCQRequest,O extends HCQResponse>
{
    public Class<I> getRequestType();
    
    public O process(HCQServerHandler context, I request) throws Exception;
}
