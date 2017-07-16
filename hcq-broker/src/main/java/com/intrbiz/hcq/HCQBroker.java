package com.intrbiz.hcq;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.intrbiz.hcq.broker.HCQBrokerImpl;
import com.intrbiz.hcq.model.AlternateExchangeInfo;
import com.intrbiz.hcq.model.BindingInfo;
import com.intrbiz.hcq.model.BrokerInfo;
import com.intrbiz.hcq.model.ClientInfo;
import com.intrbiz.hcq.model.ExchangeInfo;
import com.intrbiz.hcq.model.HCQExchange;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.NodeInfo;
import com.intrbiz.hcq.model.QueueInfo;

public interface HCQBroker
{
    public static HCQBroker get()
    {
        return HCQBrokerImpl.get();
    }
    
    // broker info
    
    NodeInfo localNodeInfo();
    
    BrokerInfo info();
    
    boolean hasQuorum();
    
    boolean waitForQuorum(long timeout, TimeUnit unit);
    
    // clients
    
    void clientConnected(ClientInfo clientInfo);
    
    void clientPinged(ClientInfo clientInfo);
    
    void clientDisconnected(ClientInfo clientInfo);
    
    Collection<ClientInfo> getConnectedClients();
    
    ClientInfo getConnectedClient(String id);
    
    Set<String> getClientTemporaryQueues(ClientInfo client);
    
    Set<String> getClientsForNode(String nodeId);
    
    //
    
    HCQQueue getOrCreateQueue(String name, int capacity, boolean autoDelete);
    
    HCQQueue getOrCreateTempQueue(String name, int capacity, ClientInfo owner);
    
    HCQQueue getQueue(String name);
    
    Set<QueueInfo> getQueueInfo();
    
    QueueInfo getQueueInfo(String name);
    
    void destroyQueue(String name);
    
    //
    
    HCQExchange getOrCreateExchange(String name, String type);
    
    default HCQExchange getOrCreateFanoutExchange(String name)
    {
        return this.getOrCreateExchange(name, HCQExchange.TYPE.FANOUT);
    }
    
    default HCQExchange getOrCreateTopicExchange(String name)
    {
        return this.getOrCreateExchange(name, HCQExchange.TYPE.TOPIC);
    }
    
    HCQExchange getExchange(String name);
    
    Set<ExchangeInfo> getExchangeInfo();
    
    ExchangeInfo getExchangeInfo(String name);
    
    void destroyExchange(String name);
    
    // bindings
    
    void bindQueueToExchange(String exchangeName, String routingKey, String targetName);
    
    default void bindQueueToExchange(String exchangeName, String targetName)
    {
        this.bindQueueToExchange(exchangeName, null, targetName);
    }
    
    void bindExchangeToExchange(String exchangeName, String routingKey, String targetName);
    
    default void bindExchangeToExchange(String exchangeName, String targetName)
    {
        this.bindExchangeToExchange(exchangeName, null, targetName);
    }
    
    Set<BindingInfo> getBindingInfo(String exchangeName);
    
    void unbindQueueToExchange(String exchangeName, String routingKey, String targetName);
    
    void unbindExchangeToExchange(String exchangeName, String routingKey, String targetName);
    
    // alternate exchanges
    
    void bindAlternateExchange(String exchangeName, String targetName);
    
    void unbindAlternateExchange(String exchangeName);
    
    AlternateExchangeInfo getAlternateExchangeInfo(String exchangeName);
}
