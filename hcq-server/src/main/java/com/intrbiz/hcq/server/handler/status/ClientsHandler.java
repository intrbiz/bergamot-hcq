package com.intrbiz.hcq.server.handler.status;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.ClientInfo;
import com.intrbiz.hcq.server.handler.StatusHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class ClientsHandler implements StatusHandler
{
    @Override
    public String getPath()
    {
        return "/status/clients";
    }

    @Override
    public DefaultFullHttpResponse process(FullHttpRequest request) throws Exception
    {
     // build the status response
        StringBuilder sb = new StringBuilder();
        sb.append("clients:\n");
        for (ClientInfo client : HCQBroker.get().getConnectedClients())
        {
            sb.append("  - id: ").append(client.getId()).append("\n");
            sb.append("    remote_address: ").append(client.getRemoteAddress()).append("\n");
            sb.append("    user_agent: ").append(client.getClientUserAgent()).append("\n");
            sb.append("    application: ").append(client.getClientApplication()).append("\n");
            sb.append("    connected_at: ").append(client.getConnected()).append("\n");
            sb.append("    last_contact: ").append(client.getLastContact()).append("\n");
            sb.append("    temporary_queues:\n");
            for (String tempQueue : HCQBroker.get().getClientTemporaryQueues(client))
            {
                sb.append("     - name: ").append(tempQueue).append("\n");    
            }
        }
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8));
    }
}
