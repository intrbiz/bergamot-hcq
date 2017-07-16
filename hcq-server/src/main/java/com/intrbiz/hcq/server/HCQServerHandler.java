package com.intrbiz.hcq.server;

import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.broker.HCQBrokerImpl;
import com.intrbiz.hcq.io.HCQTranscoder;
import com.intrbiz.hcq.model.ClientInfo;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.HCQMessage;
import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.error.HCQGeneralError;
import com.intrbiz.hcq.server.dispatch.QueueDispatcher;
import com.intrbiz.hcq.server.handler.ProtocolProcessor;
import com.intrbiz.hcq.server.handler.StatusProcessor;
import com.intrbiz.hcq.util.IdGen;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;


public class HCQServerHandler extends SimpleChannelInboundHandler<Object>
{
    private static final Logger logger = Logger.getLogger(HCQServerHandler.class);

    private static final String WEBSOCKET_PATH = "/hcq";

    private WebSocketServerHandshaker handshaker;
    
    private final HCQTranscoder transcoder = HCQTranscoder.getDefaultInstance();
    
    private final QueueDispatcher dispatcher;
    
    private InetSocketAddress remoteAddress;
    
    private InetSocketAddress localAddress;
    
    private Channel channel;
    
    private Map<String, HCQQueue> consuming = new HashMap<String, HCQQueue>();
    
    private final Timer timer;
    
    private TimerTask pingTask;
    
    private ClientInfo clientInfo;
    
    private final StatusProcessor statusProcessor;
    
    private final ProtocolProcessor protocolProcessor;
    
    public HCQServerHandler(QueueDispatcher dispatcher, StatusProcessor statusProcessor, ProtocolProcessor protocolProcessor, Timer timer)
    {
        super();
        this.dispatcher = dispatcher;
        this.statusProcessor = statusProcessor;
        this.protocolProcessor = protocolProcessor;
        this.timer = timer;
    }
    
    public SocketAddress getRemoteAddress()
    {
        return this.remoteAddress;
    }
    
    public Map<String, HCQQueue> getConsuming()
    {
        return this.consuming;
    }
    
    public ClientInfo getClientInfo()
    {
        return clientInfo;
    }

    public QueueDispatcher getDispatcher()
    {
        return dispatcher;
    }
    
    public ProtocolProcessor getProtocolProcessor()
    {
        return this.protocolProcessor;
    }

    public Channel getChannel()
    {
        return this.channel;
    }
    
    public ChannelFuture sendMessageToClient(HCQMessage message)
    {
        // ensure the message has an id
        if (message.getId() == null) message.setId(IdGen.randomId());
        // send the message
        return this.channel.writeAndFlush(new TextWebSocketFrame(this.transcoder.encodeAsString(message)));
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        this.channel = ctx.channel();
        this.remoteAddress = (InetSocketAddress) this.channel.remoteAddress();
        this.localAddress = (InetSocketAddress) this.channel.localAddress();
        // setup ping pong
        this.pingTask = new TimerTask()
        {
            public void run()
            {
                ctx.writeAndFlush(new PingWebSocketFrame());
            }
        };
        this.timer.scheduleAtFixedRate(this.pingTask, 30_000L, 30_000L);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        // cancel the ping pong task
        this.pingTask.cancel();
        // shutdown any consumers
        for (HCQQueue queue : this.consuming.values())
        {
            this.dispatcher.stopConsuming(queue, this);
        }
        // disconnect the client
        if (this.clientInfo != null) 
            HCQBroker.get().clientDisconnected(this.clientInfo);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if (msg instanceof FullHttpRequest)
        {
            FullHttpRequest http = (FullHttpRequest) msg;
            handleHttpRequest(ctx, http);
        }
        else if (msg instanceof WebSocketFrame)
        {
            WebSocketFrame frame = (WebSocketFrame) msg;
            handleWebSocketFrame(ctx, frame);
        }
        else
        {
            throw new IllegalStateException("Unexpected message, got: " + msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception
    {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess())
        {
            sendHttpResponse(ctx, req, BAD_REQUEST, "Bad Request", true);
            return;
        }
        String path = req.getUri();
        if (WEBSOCKET_PATH.equals(path))
        {
            // get client application details
            String userAgent = req.headers().get(USER_AGENT);
            String clientApplication = req.headers().get("X-HCQ-Client-Application");
            // validate the application details
            if (Util.isEmpty(userAgent) || Util.isEmpty(clientApplication))
            {
                sendHttpResponse(ctx, req, BAD_REQUEST, "UserAgent and X-HCQ-Client-Application missing", true);
                return;
            }
            // assert we have qurorum
            if (! HCQBroker.get().hasQuorum())
            {
                sendHttpResponse(ctx, req, INTERNAL_SERVER_ERROR, "This server does not yet have quorum, come back later!", true);
                return;    
            }
            // get the client details
            this.clientInfo = new ClientInfo();
            this.clientInfo.setId(this.remoteAddress.getHostString() + ":" + this.remoteAddress.getPort() + "->" + this.localAddress.getHostString() + ":" + this.localAddress.getPort());
            this.clientInfo.setRemoteAddress(this.remoteAddress.getHostString() + ":" + this.remoteAddress.getPort());
            this.clientInfo.setClientUserAgent(userAgent);
            this.clientInfo.setClientApplication(clientApplication);
            this.clientInfo.setConnected(System.currentTimeMillis());
            this.clientInfo.setLastContact(this.clientInfo.getConnected());
            HCQBroker.get().clientConnected(this.clientInfo);
            // start the WS handshake
            if (logger.isTraceEnabled()) logger.trace("Handshaking websocket request url: " + req.getUri());
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
            this.handshaker = wsFactory.newHandshaker(req);
            if (this.handshaker == null)
            {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }
            else
            {
                HttpHeaders headers = new DefaultHttpHeaders();
                headers.set(HttpHeaders.Names.HOST, req.headers().get(HttpHeaders.Names.HOST));
                headers.set(HttpHeaders.Names.SERVER, HCQBrokerImpl.SERVER);
                this.handshaker.handshake(ctx.channel(), req, headers, ctx.channel().newPromise());
            }
        }
        else
        {
            // use the status processor to process the request
            DefaultFullHttpResponse response = this.statusProcessor.process(path, req);
            setHost(response, getHost(req));
            response.headers().set(HttpHeaders.Names.SERVER, HCQBrokerImpl.SERVER);
            // write flush and close
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleWebSocketFrame(final ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception
    {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame)
        {
            this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // ping pong
        if (frame instanceof PingWebSocketFrame)
        {
            // update our client state
            this.clientInfo.setLastContact(System.currentTimeMillis());
            HCQBroker.get().clientPinged(this.clientInfo);
            // respond to the ping
            ctx.channel().writeAndFlush(new PongWebSocketFrame());
            return;
        }
        if (frame instanceof PongWebSocketFrame)
        {
            // update our client state
            this.clientInfo.setLastContact(System.currentTimeMillis());
            HCQBroker.get().clientPinged(this.clientInfo);
            return;
        }
        // only support text frames
        if (frame instanceof TextWebSocketFrame)
        {
            // get the frame
            HCQMessage request = this.transcoder.decodeFromString(((TextWebSocketFrame) frame).text(), HCQMessage.class);
            // process the message and respond
            if (request instanceof HCQRequest)
            {
                HCQMessage response = this.protocolProcessor.process(this, (HCQRequest) request);
                if (response != null)
                {
                    ctx.writeAndFlush(new TextWebSocketFrame(transcoder.encodeAsString(response)));    
                }   
            }
            else
            {
                ctx.writeAndFlush(new TextWebSocketFrame(transcoder.encodeAsString(new HCQGeneralError(request, "Bad request"))));
            }
        }
    }
    
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, HttpResponseStatus status, String message, boolean forceClose)
    {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
        // set some headers
        setContentLength(res, res.content().readableBytes());
        setHost(res, getHost(req));
        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.writeAndFlush(res);
        if (forceClose) f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.error("Error processing request", cause);
        ctx.close();
    }

    private static String getWebSocketLocation(FullHttpRequest req)
    {
        return "wss://" + req.headers().get(HOST) + WEBSOCKET_PATH;
    }
}