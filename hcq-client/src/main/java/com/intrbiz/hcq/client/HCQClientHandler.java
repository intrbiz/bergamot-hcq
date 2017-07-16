package com.intrbiz.hcq.client;

import java.net.URI;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.intrbiz.hcq.io.HCQTranscoder;
import com.intrbiz.hcq.model.message.HCQCallback;
import com.intrbiz.hcq.model.message.HCQMessage;
import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.model.message.error.HCQGeneralError;
import com.intrbiz.util.HCQClientFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class HCQClientHandler extends SimpleChannelInboundHandler<Object>
{    
    private Logger logger = Logger.getLogger(HCQClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    
    private final HCQTranscoder transcoder = HCQTranscoder.getDefaultInstance();
    
    private final ConcurrentMap<String, Consumer<HCQResponse>> pendingCallbacks = new ConcurrentHashMap<String, Consumer<HCQResponse>>();
    
    private final CountDownLatch handshakeLatch = new CountDownLatch(1);
    
    private final LinkedList<HCQRequest> queuedMessages = new LinkedList<HCQRequest>();
    
    private Channel channel;
    
    private volatile boolean closed = false;
    
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Consumer<HCQCallback>>> callbackConsumers = new ConcurrentHashMap<String, CopyOnWriteArrayList<Consumer<HCQCallback>>>();
    
    private Runnable onDisconnect = null;

    public HCQClientHandler(URI server, String clientApplication)
    {
        super();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaders.Names.HOST, server.getHost() + ":" + server.getPort());
        headers.set(HttpHeaders.Names.USER_AGENT, "hcq/1.0.0");
        headers.set("X-HCQ-Client-Application", clientApplication);
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(server, WebSocketVersion.V13, null, false, headers);
    }
    
    public void onDisconnect(Runnable onDisconnect)
    {
        this.onDisconnect = onDisconnect;
    }
    
    public boolean isClosed()
    {
        return this.closed;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        logger.trace("Connected, starting handshake");
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        this.closed = true;
        // flush all response handlers
        for (Entry<String, Consumer<HCQResponse>> entry : this.pendingCallbacks.entrySet())
        {
            entry.getValue().accept(new HCQGeneralError(entry.getKey(), "Connection closed before request was completed"));
        }
        this.pendingCallbacks.clear();
        // fire on disconnect callback
        if (this.onDisconnect != null)
        {
            ctx.executor().execute(this.onDisconnect);
        }
    }

    public void channelHandshaked(ChannelHandlerContext ctx)
    {
        logger.trace("Handshake done");
        this.channel = ctx.channel();
        this.handshakeLatch.countDown();
        // flush the messages
        synchronized (this)
        {
            for (HCQMessage message : this.queuedMessages)
            {
                this.sendMessage(message);
            }
            this.queuedMessages.clear();
        }
    }
    
    private ChannelFuture sendMessage(HCQMessage message)
    {
        return this.channel.writeAndFlush(new TextWebSocketFrame(this.transcoder.encodeAsString(message)));
    }
    
    public <C extends Consumer<HCQResponse>> C sendMessageToServer(HCQRequest message, C callback)
    {
        if (this.logger.isTraceEnabled()) this.logger.trace("Sending " + message+ " with callback " + callback);
        if (this.closed)
        {
            throw new RuntimeException("Client is closed");
        }
        // add the callback
        if (callback != null) this.pendingCallbacks.put(message.getId(), callback);
        // send the message
        if (this.handshakeLatch.getCount() > 0)
        {
            synchronized (this)
            {
                this.queuedMessages.add(message);
            }
        }
        else
        {
            this.sendMessage(message);
        }
        return callback;
    }
    
    public void registerCallbackConsumer(String tag, Consumer<HCQCallback> callback)
    {
        CopyOnWriteArrayList<Consumer<HCQCallback>> callbacks = this.callbackConsumers.computeIfAbsent(tag, (k) -> new CopyOnWriteArrayList<Consumer<HCQCallback>>());
        callbacks.add(callback);
    }
    
    public void unregisterCallbackConsumer(String tag)
    {
        CopyOnWriteArrayList<Consumer<HCQCallback>> callbacks = this.callbackConsumers.computeIfAbsent(tag, (k) -> new CopyOnWriteArrayList<Consumer<HCQCallback>>());
        callbacks.clear();
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Object msg)
    {
        if (msg instanceof FullHttpResponse)
        {
            FullHttpResponse http = (FullHttpResponse) msg;
            // complete the handshake
            if (!handshaker.isHandshakeComplete())
            {
                handshaker.finishHandshake(ctx.channel(), http);
                this.channelHandshaked(ctx);
                return;
            }
        }
        else if (msg instanceof WebSocketFrame)
        {
            // process the frame
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame)
            {
                String payload = ((TextWebSocketFrame) frame).text();
                if (logger.isTraceEnabled()) logger.trace("Got message from HCQ server: " + payload);
                try
                {
                    HCQMessage message = this.transcoder.decodeFromString(payload, HCQMessage.class);
                    // process the response or message
                    if (message instanceof HCQCallback)
                    {
                        // dispatch callback
                        HCQCallback callback = (HCQCallback) message;
                        String tag = callback.getRoutingTag();
                        CopyOnWriteArrayList<Consumer<HCQCallback>> callbackConsumers = this.callbackConsumers.get(tag);
                        if (callbackConsumers != null)
                        {
                            for (Consumer<HCQCallback> callbackConsumer : callbackConsumers)
                            {
                                callbackConsumer.accept(callback);
                            }
                        }
                    }
                    else if (message instanceof HCQResponse)
                    {
                        final HCQResponse response = (HCQResponse) message;
                        // get the callback
                        final Consumer<HCQResponse> consumer = pendingCallbacks.remove(response.getId());
                        // run futures directly
                        if (consumer instanceof HCQClientFuture)
                        {
                            consumer.accept(response);
                        }
                        else if (consumer != null)
                        {
                            // execute the callback in our worker thread pool
                            ctx.executor().execute(() -> consumer.accept(response));
                        }
                    }
                    else
                    {
                        ctx.writeAndFlush(new TextWebSocketFrame(this.transcoder.encodeAsString(new HCQGeneralError(message, "Bad request"))));
                    }
                }
                catch (Exception e)
                {
                    logger.error("Failed to decode request", e);
                    ctx.writeAndFlush(new TextWebSocketFrame(this.transcoder.encodeAsString(new HCQGeneralError("Failed to decode request"))));
                }
            }
            else if (frame instanceof PingWebSocketFrame)
            {
                logger.trace("Got ping from server");
                ctx.writeAndFlush(new PongWebSocketFrame());
            }
            else if (frame instanceof CloseWebSocketFrame)
            {
                logger.trace("Closing connection");
                ctx.close();
            }
        }
        else
        {
            throw new IllegalStateException("Unexpected message, got: " + msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
    {
        logger.error("Unhandled error communicating with HCQ server", e);
        ctx.close();
    }
    
    public void close()
    {
        if (! this.closed)
        {
            this.closed = true;
            this.channel.close();
        }
    }
}
