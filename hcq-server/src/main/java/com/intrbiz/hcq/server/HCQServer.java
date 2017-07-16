package com.intrbiz.hcq.server;

import java.util.Timer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.server.dispatch.QueueDispatcher;
import com.intrbiz.hcq.server.handler.ProtocolProcessor;
import com.intrbiz.hcq.server.handler.StatusProcessor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class HCQServer implements Runnable
{
    private Logger logger = Logger.getLogger(HCQServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Thread runner = null;
    
    private final QueueDispatcher dispatcher = new QueueDispatcher();
    
    private final Timer timer = new Timer();
    
    private final int port;
    
    private Channel serverChannel;
    
    private final StatusProcessor statusProcessor = new StatusProcessor();
    
    private final ProtocolProcessor protocolProcessor = new ProtocolProcessor();

    public HCQServer(int port)
    {
        super();
        this.port = port;
    }
    
    public QueueDispatcher getDispatcher()
    {
        return this.dispatcher;
    }

    public void run()
    {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator());
            b.childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                public void initChannel(SocketChannel ch) throws Exception
                {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("read-timeout",  new ReadTimeoutHandler(  90 /* seconds */ )); 
                    pipeline.addLast("write-timeout", new WriteTimeoutHandler( 90 /* seconds */ ));
                    pipeline.addLast("codec-http",    new HttpServerCodec());
                    pipeline.addLast("aggregator",    new HttpObjectAggregator(1024 * 1024));
                    pipeline.addLast("handler",       new HCQServerHandler(dispatcher, statusProcessor, protocolProcessor, timer));
                }
            });
            //
            this.serverChannel = b.bind(this.port).sync().channel();
            logger.info("Web socket server started at port " + this.port + '.');
            // await the server to stop
            this.serverChannel.closeFuture().sync();
            // log
            logger.info("HCQ server has shutdown");
        }
        catch (Exception e)
        {
            logger.error("HCQ server broke", e);
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void start()
    {
        if (this.runner == null)
        {
            this.runner = new Thread(this);
            this.runner.start();
        }
    }
    
    public void stop()
    {
        this.serverChannel.close().awaitUninterruptibly();
    }
    
    public static void main(String[] args) throws Exception
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);
        // start the broker
        System.out.println("Local: " + HCQBroker.get().localNodeInfo());
        System.out.println("Broker: " + HCQBroker.get().info());
        // setup the server
        HCQServer server = new HCQServer(Integer.parseInt(args[0]));
        // go go go
        server.start();
    }
}
