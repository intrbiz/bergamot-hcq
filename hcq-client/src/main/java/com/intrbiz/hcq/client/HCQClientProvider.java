package com.intrbiz.hcq.client;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class HCQClientProvider
{    
    private static final Logger logger = Logger.getLogger(HCQClientProvider.class);
    
    private EventLoopGroup eventLoop = new NioEventLoopGroup();
    
    private URI[] servers;
    
    private long serverIndex = 0;
    
    public HCQClientProvider(URI[] servers)
    {
        super();
        this.servers = servers;
    }
    
    public HCQClientProvider(URI server)
    {
        this(new URI[] { server });
    }
    
    public URI[] getServers()
    {
        return this.servers;
    }
    
    public URI getServer()
    {
        return this.servers[(int) (this.serverIndex++ % this.servers.length)];
    }

    public HCQClient connect(String clientApplication) throws Exception
    {
        final URI server = this.getServer();
        HCQClientHandler handler = new HCQClientHandler(server, clientApplication); 
        Bootstrap b = new Bootstrap();
        b.group(this.eventLoop);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
        b.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                // HTTP handling
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("read-timeout",  new ReadTimeoutHandler(  90 /* seconds */ )); 
                pipeline.addLast("write-timeout", new WriteTimeoutHandler( 90 /* seconds */ ));
                pipeline.addLast("codec",         new HttpClientCodec()); 
                pipeline.addLast("aggregator",    new HttpObjectAggregator(1024 * 1024));
                pipeline.addLast("handler",       handler);
            }
        });
        // connect the client
        logger.info("Connecting to: " + server);
        ChannelFuture future = b.connect(server.getHost(), server.getPort());
        return new HCQClient(future, handler);
        
    }

    public void shutdownAllClients()
    {
        try
        {
            this.eventLoop.shutdownGracefully().await();
        }
        catch (InterruptedException e)
        {
        }
    }
    
    public void terminateAllClients()
    {
        try
        {
            this.eventLoop.shutdownGracefully(1, 2, TimeUnit.SECONDS).await();
        }
        catch (InterruptedException e)
        {
        }
    }
}
