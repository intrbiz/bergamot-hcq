package com.intrbiz.hcq.server.handler.status;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.AlternateExchangeInfo;
import com.intrbiz.hcq.model.BindingInfo;
import com.intrbiz.hcq.model.ExchangeInfo;
import com.intrbiz.hcq.server.handler.StatusHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class ExchangesHandler implements StatusHandler
{
    @Override
    public String getPath()
    {
        return "/status/exchanges";
    }

    @Override
    public DefaultFullHttpResponse process(FullHttpRequest request) throws Exception
    {
     // build the status response
        StringBuilder sb = new StringBuilder();
        sb.append("exchanges:\n");
        for (ExchangeInfo exchange : HCQBroker.get().getExchangeInfo())
        {
            sb.append("  - name: ").append(exchange.getName()).append("\n");
            sb.append("    type: ").append(exchange.getType()).append("\n");
            sb.append("    created_at: ").append(exchange.getCreatedAt()).append("\n");
            sb.append("    bindings:\n");
            for (BindingInfo binding : HCQBroker.get().getBindingInfo(exchange.getName()))
            {
                sb.append("     - key: ").append(binding.getBinding()).append("\n");
                sb.append("       target_type: ").append(binding.getTargetType()).append("\n");
                sb.append("       target_name: ").append(binding.getTargetName()).append("\n");
            }
            AlternateExchangeInfo ae = HCQBroker.get().getAlternateExchangeInfo(exchange.getName());
            if (ae != null)
            {
                sb.append("    alternate_exchange: ").append(ae.getTargetName()).append("\n");
            }
        }
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8));
    }
}
