package com.intrbiz.hcq.server.handler.status;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.model.QueueInfo;
import com.intrbiz.hcq.server.handler.StatusHandler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

public class QueuesHandler implements StatusHandler
{
    @Override
    public String getPath()
    {
        return "/status/queues";
    }

    @Override
    public DefaultFullHttpResponse process(FullHttpRequest request) throws Exception
    {
     // build the status response
        StringBuilder sb = new StringBuilder();
        sb.append("queues:\n");
        for (QueueInfo queue : HCQBroker.get().getQueueInfo())
        {
            sb.append("  - name: ").append(queue.getName()).append("\n");
            sb.append("    auto_delete: ").append(queue.isAutoDelete()).append("\n");
            sb.append("    temporary: ").append(queue.isTemporary()).append("\n");
            sb.append("    created_at: ").append(queue.getCreatedAt()).append("\n");
        }
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8));
    }
}
