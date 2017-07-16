package com.intrbiz.hcq.server.handler.status;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.BrokerInfo;
import com.intrbiz.hcq.model.NodeInfo;
import com.intrbiz.hcq.server.handler.StatusHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class InfoStatusHandler implements StatusHandler
{
    @Override
    public String getPath()
    {
        return "/status/info";
    }

    @Override
    public DefaultFullHttpResponse process(FullHttpRequest request) throws Exception
    {
     // build the status response
        StringBuilder sb = new StringBuilder();
        BrokerInfo info = HCQBroker.get().info();
        sb.append("name: ").append(info.getName()).append("\n");
        sb.append("server: ").append(info.getServer()).append("\n");
        sb.append("quorum: ").append(info.isQuorum()).append("\n");
        sb.append("nodes:\n");
        for (NodeInfo node : info.getNodes())
        {
            sb.append("  - id: ").append(node.getId()).append("\n");
            sb.append("    hostname: ").append(node.getHostName()).append("\n");
            sb.append("    clients:\n");
            for (String clientId : HCQBroker.get().getClientsForNode(node.getId()))
            {
                sb.append("     - id: ").append(clientId).append("\n");   
            }
        }
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8));
    }
}
