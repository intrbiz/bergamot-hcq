package com.intrbiz.hcq.client;

import java.util.function.Consumer;

import com.intrbiz.hcq.model.message.batch.BatchComplete;
import com.intrbiz.hcq.model.message.batch.BatchRequest;
import com.intrbiz.hcq.model.message.error.HCQError;
import com.intrbiz.hcq.model.message.exchange.BindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BoundExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyedExchange;
import com.intrbiz.hcq.model.message.exchange.GetOrCreateExchange;
import com.intrbiz.hcq.model.message.exchange.GotExchange;
import com.intrbiz.hcq.model.message.exchange.PublishMessageToExchange;
import com.intrbiz.hcq.model.message.exchange.PublishedMessageToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundQueueToExchange;
import com.intrbiz.hcq.model.message.queue.DestroyQueue;
import com.intrbiz.hcq.model.message.queue.DestroyedQueue;
import com.intrbiz.hcq.model.message.queue.GetOrCreateQueue;
import com.intrbiz.hcq.model.message.queue.GotQueue;
import com.intrbiz.hcq.model.message.queue.PublishMessageToQueue;
import com.intrbiz.hcq.model.message.queue.PublishedMessageToQueue;
import com.intrbiz.hcq.model.message.queue.ReceiveMessageFromQueue;
import com.intrbiz.hcq.model.message.queue.StartConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StartedConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StopConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StoppedConsumingQueue;
import com.intrbiz.hcq.model.message.type.QueuedMessage;
import com.intrbiz.util.ErrorHandlingCallback;
import com.intrbiz.util.HCQClientFuture;

import io.netty.channel.ChannelFuture;

public class HCQClient implements AutoCloseable
{
    private final ChannelFuture future;
    
    private final HCQClientHandler handler;
    
    public HCQClient(ChannelFuture future, HCQClientHandler handler)
    {
        this.future = future;
        this.handler = handler;
    }
    
    public boolean isConnected()
    {
        return future.isSuccess();
    }
    
    public void waitConnected() throws Exception
    {
        this.future.sync();
    }
    
    public void onDisconnect(Runnable onDisconnect)
    {
        this.handler.onDisconnect(onDisconnect);
    }
    
    public void close()
    {
        this.handler.close();
    }
    
    protected void checkOpen()
    {
        if (this.handler.isClosed())
            throw new IllegalStateException("HCQClient is closed");
    }
    
    // batch
    
    public HCQBatch batch()
    {
        return new HCQBatch()
        {
            protected void submit(BatchRequest request, Consumer<BatchComplete> onResponse, Consumer<HCQError> onError)
            {
                checkOpen();
                handler.sendMessageToServer(request, new ErrorHandlingCallback<BatchComplete>(onResponse, onError));
            }
            
            protected HCQClientFuture<BatchComplete> submit(BatchRequest request)
            {
                checkOpen();
                return handler.sendMessageToServer(request, new HCQClientFuture<BatchComplete>());
            }
        };
    }
    
    // queues
    
    public void getOrCreateQueue(String queueName, int capacity, boolean autoDelete, Consumer<GotQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new GetOrCreateQueue(queueName, capacity, autoDelete), new ErrorHandlingCallback<GotQueue>(onResponse, onError));
    }
    
    public HCQClientFuture<GotQueue> getOrCreateQueue(String queueName, int capacity, boolean autoDelete)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new GetOrCreateQueue(queueName, capacity, autoDelete), new HCQClientFuture<GotQueue>());
    }
    
    public void getOrCreateTempQueue(String queueName, int capacity, Consumer<GotQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new GetOrCreateQueue(queueName, capacity, true, true), new ErrorHandlingCallback<GotQueue>(onResponse, onError));
    }
    
    public HCQClientFuture<GotQueue> getOrCreateTempQueue(String queueName, int capacity)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new GetOrCreateQueue(queueName, capacity, true, true), new HCQClientFuture<GotQueue>());
    }
    
    public void startConsumingQueue(String queueName, Consumer<ReceiveMessageFromQueue> consumer, Consumer<StartedConsumingQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.registerCallbackConsumer(queueName, (c) -> { if (consumer != null && c instanceof ReceiveMessageFromQueue) consumer.accept((ReceiveMessageFromQueue) c); });
        this.handler.sendMessageToServer(new StartConsumingQueue(queueName), new ErrorHandlingCallback<StartedConsumingQueue>(onResponse, onError));
    }
    
    public HCQClientFuture<StartedConsumingQueue> startConsumingQueue(String queueName, Consumer<ReceiveMessageFromQueue> consumer)
    {
        this.checkOpen();
        this.handler.registerCallbackConsumer(queueName, (c) -> { if (consumer != null && c instanceof ReceiveMessageFromQueue) consumer.accept((ReceiveMessageFromQueue) c); });
        return this.handler.sendMessageToServer(new StartConsumingQueue(queueName), new HCQClientFuture<StartedConsumingQueue>());
    }
    
    public void publishMessageToQueue(String queueName, QueuedMessage message, Consumer<PublishedMessageToQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new PublishMessageToQueue(queueName, message), new ErrorHandlingCallback<PublishedMessageToQueue>(onResponse, onError));
    }
    
    public HCQClientFuture<PublishedMessageToQueue> publishMessageToQueue(String queueName, QueuedMessage message)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new PublishMessageToQueue(queueName, message), new HCQClientFuture<PublishedMessageToQueue>());
    }
    
    public void destroyQueue(String queueName, Consumer<DestroyedQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new DestroyQueue(queueName), new ErrorHandlingCallback<DestroyedQueue>(onResponse, onError));
    }
    
    public HCQClientFuture<DestroyedQueue> destroyQueue(String queueName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new DestroyQueue(queueName), new HCQClientFuture<DestroyedQueue>());
    }
    
    public void stopConsumingQueue(String queueName, Consumer<ReceiveMessageFromQueue> consumer, Consumer<StoppedConsumingQueue> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new StopConsumingQueue(queueName), new ErrorHandlingCallback<StoppedConsumingQueue>(onResponse, onError).andThen((m) -> this.handler.unregisterCallbackConsumer(queueName)));
    }
    
    public HCQClientFuture<StoppedConsumingQueue> stopConsumingQueue(String queueName)
    {
        this.checkOpen();
        HCQClientFuture<StoppedConsumingQueue> future = new HCQClientFuture<StoppedConsumingQueue>();
        this.handler.sendMessageToServer(new StopConsumingQueue(queueName), future.andThen((m) -> this.handler.unregisterCallbackConsumer(queueName)));
        return future;
    }
    
    // exchanges
    
    public void getOrCreateExchange(String exchangeName, String exchangeType, Consumer<GotExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new GetOrCreateExchange(exchangeName, exchangeType), new ErrorHandlingCallback<GotExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<GotExchange> getOrCreateExchange(String exchangeName, String exchangeType)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new GetOrCreateExchange(exchangeName, exchangeType), new HCQClientFuture<GotExchange>());
    }
    
    public void publishMessageToExchange(String exchangeName, String routingKey, QueuedMessage message, Consumer<PublishedMessageToExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new PublishMessageToExchange(exchangeName, routingKey, message), new ErrorHandlingCallback<PublishedMessageToExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<PublishedMessageToExchange> publishMessageToExchange(String exchangeName, String routingKey, QueuedMessage message)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new PublishMessageToExchange(exchangeName, routingKey, message), new HCQClientFuture<PublishedMessageToExchange>());
    }
    
    public void destroyExchange(String exchangeName, Consumer<DestroyedExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new DestroyExchange(exchangeName), new ErrorHandlingCallback<DestroyedExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<DestroyedExchange> destroyExchange(String exchangeName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new DestroyExchange(exchangeName), new HCQClientFuture<DestroyedExchange>());
    }
    
    // bindings
    
    public void bindQueueToExchange(String exchangeName, String key, String targetName, Consumer<BoundQueueToExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new BindQueueToExchange(exchangeName, key, targetName), new ErrorHandlingCallback<BoundQueueToExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<BoundQueueToExchange> bindQueueToExchange(String exchangeName, String key, String targetName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new BindQueueToExchange(exchangeName, key, targetName), new HCQClientFuture<BoundQueueToExchange>());
    }
    
    public void bindExchangeToExchange(String exchangeName, String key, String targetName, Consumer<BoundExchangeToExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new BindExchangeToExchange(exchangeName, key, targetName), new ErrorHandlingCallback<BoundExchangeToExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<BoundExchangeToExchange> bindExchangeToExchange(String exchangeName, String key, String targetName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new BindExchangeToExchange(exchangeName, key, targetName), new HCQClientFuture<BoundExchangeToExchange>());
    }
    
    public void unbindQueueToExchange(String exchangeName, String key, String targetName, Consumer<UnboundQueueToExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new UnbindQueueToExchange(exchangeName, key, targetName), new ErrorHandlingCallback<UnboundQueueToExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<UnboundQueueToExchange> unbindQueueToExchange(String exchangeName, String key, String targetName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new UnbindQueueToExchange(exchangeName, key, targetName), new HCQClientFuture<UnboundQueueToExchange>());
    }
    
    public void unbindExchangeToExchange(String exchangeName, String key, String targetName, Consumer<UnboundExchangeToExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new UnbindExchangeToExchange(exchangeName, key, targetName), new ErrorHandlingCallback<UnboundExchangeToExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<UnboundExchangeToExchange> unbindExchangeToExchange(String exchangeName, String key, String targetName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new UnbindExchangeToExchange(exchangeName, key, targetName), new HCQClientFuture<UnboundExchangeToExchange>());
    }
    
    // alternate exchange
    
    public void bindAlternateExchange(String exchangeName, String targetName, Consumer<BoundAlternateExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new BindAlternateExchange(exchangeName, targetName), new ErrorHandlingCallback<BoundAlternateExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<BoundAlternateExchange> bindAlternateExchange(String exchangeName, String targetName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new BindAlternateExchange(exchangeName, targetName), new HCQClientFuture<BoundAlternateExchange>());
    }
    
    public void unbindAlternateExchange(String exchangeName, Consumer<UnboundAlternateExchange> onResponse, Consumer<HCQError> onError)
    {
        this.checkOpen();
        this.handler.sendMessageToServer(new UnbindAlternateExchange(exchangeName), new ErrorHandlingCallback<UnboundAlternateExchange>(onResponse, onError));
    }
    
    public HCQClientFuture<UnboundAlternateExchange> unbindAlternateExchange(String exchangeName)
    {
        this.checkOpen();
        return this.handler.sendMessageToServer(new UnbindAlternateExchange(exchangeName), new HCQClientFuture<UnboundAlternateExchange>());
    }
}
