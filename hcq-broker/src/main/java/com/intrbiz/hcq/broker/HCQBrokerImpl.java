package com.intrbiz.hcq.broker;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.MultiMapConfig.ValueCollectionType;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.quorum.Quorum;
import com.hazelcast.quorum.QuorumType;
import com.intrbiz.Util;
import com.intrbiz.hcq.HCQBroker;
import com.intrbiz.hcq.broker.util.ValueEqualsPredicate;
import com.intrbiz.hcq.model.AlternateExchangeInfo;
import com.intrbiz.hcq.model.BindingInfo;
import com.intrbiz.hcq.model.BrokerInfo;
import com.intrbiz.hcq.model.ClientInfo;
import com.intrbiz.hcq.model.ExchangeInfo;
import com.intrbiz.hcq.model.HCQExchange;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.NodeInfo;
import com.intrbiz.hcq.model.QueueInfo;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

public class HCQBrokerImpl implements HCQBroker, MembershipListener
{   
    private static final int MAX_QUEUE_CAPACITY_LOG = 5;
    
    public static final String SERVER = "hcq/1.0.0";
    
    private static final HCQBrokerImpl US = new HCQBrokerImpl();
    
    public static final HCQBrokerImpl get()
    {
        return US;
    }
    
    private HazelcastInstance hazelcastInstance;
    
    private IMap<String, QueueMeta> queueMetadata;
    
    private IMap<String, ExchangeMeta> exchangeMetadata;
    
    private MultiMap<String, BindingMeta> exchangeBindings;
    
    private IMap<String, String> exchangeAlternates;
    
    private MultiMap<String, String> queueToExchangeBindings;
    
    private MultiMap<String, String> exchangeToExchangeBindings;
    
    private Map<String, ClientInfo> clients;
    
    private MultiMap<String, String> clientTemporaryQueues;
    
    private MultiMap<String, String> memberClients;
    
    @SuppressWarnings("unused")
    private String membershipListerId;
    
    private Quorum quorum;
    
    protected HCQBrokerImpl()
    {
        try
        {
            Config config;
            // setup config
            String hazelcastConfigFile = Util.coalesceEmpty(System.getProperty("hazelcast.config"), System.getenv("hazelcast_config"));
            if (hazelcastConfigFile != null)
            {
                config = new XmlConfigBuilder(hazelcastConfigFile).build();
            }
            else
            {
                config = new Config();
            }
            // add our custom config
            // setup metadata maps
            MapConfig metaMapConfig = config.getMapConfig("hcq.meta.*");
            metaMapConfig.setAsyncBackupCount(2);
            metaMapConfig.setBackupCount(1);
            metaMapConfig.setEvictionPolicy(EvictionPolicy.NONE);
            MultiMapConfig metaMultiMapConfig = config.getMultiMapConfig("hcq.meta.*");
            metaMultiMapConfig.setAsyncBackupCount(2);
            metaMultiMapConfig.setBackupCount(1);
            metaMultiMapConfig.setValueCollectionType(ValueCollectionType.SET);
            // setup queues
            List<QueueConfig> queues = new LinkedList<QueueConfig>();
            for (int i = 1; i < MAX_QUEUE_CAPACITY_LOG; i++)
            {
                // queue config
                QueueConfig queueConfig = config.getQueueConfig("hcq." + i + ".*");
                queueConfig.setBackupCount(1);
                queueConfig.setMaxSize((int) Math.pow(10, i));
                queueConfig.setStatisticsEnabled(true);
                // temp queue config
                QueueConfig tempQueueConfig = config.getQueueConfig("hcq.temp." + i + ".*");
                tempQueueConfig.setBackupCount(0);
                tempQueueConfig.setMaxSize((int) Math.pow(10, i));
                tempQueueConfig.setStatisticsEnabled(true);
            }
            // setup quorum
            boolean quorum = "true".equalsIgnoreCase(Util.coalesceEmpty(System.getProperty("hcq.quorum"), System.getenv("hcq_quorum"), "true"));
            if (quorum)
            {
                QuorumConfig hcqQuorum = config.getQuorumConfig("hcq");
                hcqQuorum.setType(QuorumType.READ_WRITE);
                hcqQuorum.setEnabled(true);
                hcqQuorum.setSize(Integer.parseInt(Util.coalesceEmpty(System.getProperty("hcq.quorum.size"), System.getenv("hcq_quorum_size"), "2")));
                // setup for maps
                metaMapConfig.setQuorumName("hcq");
                for (QueueConfig queueConfig : queues)
                {
                    queueConfig.setQuorumName("hcq");
                }
            }
            // set the instance name
            config.setInstanceName("hcq");
            // create the instance
            this.hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
            // setup the metadata we need
            if (quorum) this.quorum         = this.hazelcastInstance.getQuorumService().getQuorum("hcq");
            this.queueMetadata              = this.hazelcastInstance.getMap("hcq.meta.queue");
            this.exchangeMetadata           = this.hazelcastInstance.getMap("hcq.meta.exchange");
            this.exchangeBindings           = this.hazelcastInstance.getMultiMap("hcq.meta.exchange.binding");
            this.exchangeAlternates         = this.hazelcastInstance.getMap("hcq.meta.exchange.alternate");
            this.queueToExchangeBindings    = this.hazelcastInstance.getMultiMap("hcq.meta.queue.toexchange");
            this.exchangeToExchangeBindings = this.hazelcastInstance.getMultiMap("hcq.meta.exchange.toexchange");
            this.clients                    = this.hazelcastInstance.getMap("hcq.meta.clients");
            this.clientTemporaryQueues      = this.hazelcastInstance.getMultiMap("hcq.meta.client.temporary.queues");
            this.memberClients              = this.hazelcastInstance.getMultiMap("hcq.meta.member.clients");
            // listen to member events
            this.membershipListerId = this.hazelcastInstance.getCluster().addMembershipListener(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to start Hazelcast", e);
        }
    }
    
    // broker
    
    public NodeInfo localNodeInfo()
    {
        Member local = this.hazelcastInstance.getCluster().getLocalMember();
        return new NodeInfo(local.getUuid(), local.getAddress().getHost() + ":" + local.getAddress().getPort());
    }
    
    public BrokerInfo info()
    {
        List<NodeInfo> nodes = new LinkedList<NodeInfo>();
        for (Member member :this.hazelcastInstance.getCluster().getMembers())
        {
            nodes.add(new NodeInfo(member.getUuid(), member.getAddress().getHost() + ":" + member.getAddress().getPort()));
        }
        return new BrokerInfo("hcq", SERVER, this.hasQuorum(), nodes);
    }

    @Override
    public boolean hasQuorum()
    {
        return this.quorum == null ? true : this.quorum.isPresent();
    }
    
    @Override
    public boolean waitForQuorum(long timeout, TimeUnit unit)
    {
        if (this.quorum == null) return true;
        // wait for quorum
        long start = System.nanoTime();
        long end   = unit.toNanos(timeout) + start;
        while (System.nanoTime() < end)
        {
            if (this.hasQuorum())
                return true;
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
        
        return false;
    }
    
    @Override
    public void clientConnected(ClientInfo clientInfo)
    {
        Member local = this.hazelcastInstance.getCluster().getLocalMember();
        System.out.println("Client connected: " + clientInfo + " to member " + local.getUuid());
        this.memberClients.put(local.getUuid(), clientInfo.getId());
        this.clients.put(clientInfo.getId(), clientInfo);
    }
    
    @Override
    public void clientPinged(ClientInfo clientInfo)
    {
        Member local = this.hazelcastInstance.getCluster().getLocalMember();
        System.out.println("Client pinged: " + clientInfo + " to member " + local.getUuid());
        this.memberClients.put(local.getUuid(), clientInfo.getId());
        ClientInfo oldInfo = this.clients.putIfAbsent(clientInfo.getId(), clientInfo);
        oldInfo.setLastContact(clientInfo.getLastContact());
        this.clients.put(clientInfo.getId(), oldInfo);
    }
    
    @Override
    public void clientDisconnected(ClientInfo clientInfo)
    {
        Member local = this.hazelcastInstance.getCluster().getLocalMember();
        System.out.println("Client disconnected: " + clientInfo + " from member " + local.getUuid());
        this.cleanupClient(local.getUuid(), clientInfo.getId());
    }
    
    protected void cleanupClient(String memberId, String clientId)
    {
        ClientInfo info = this.clients.remove(clientId);
        System.out.println("Cleaning up client: " + info + " of member " + memberId);
        // remove any temp queues
        for (String tempQueue : this.clientTemporaryQueues.get(clientId))
        {
            System.out.println("Destroying temp queue: " + tempQueue);
            this.destroyQueue(tempQueue);
        }
        this.clientTemporaryQueues.remove(clientId);
        this.memberClients.remove(memberId, clientId);
    }
    
    @Override
    public Collection<ClientInfo> getConnectedClients()
    {
        return new HashSet<ClientInfo>(this.clients.values());
    }
    
    public ClientInfo getConnectedClient(String id)
    {
        return this.clients.get(id);
    }
    
    public Set<String> getClientTemporaryQueues(ClientInfo client)
    {
        return new HashSet<String>(this.clientTemporaryQueues.get(client.getId()));
    }
    
    public Set<String> getClientsForNode(String nodeId)
    {
        return new HashSet<String>(this.memberClients.get(nodeId));
    }
    
    // queues
    
    private static String computeQueueName(boolean temp, String name, int capacity)
    {
        return (temp ? "hcq.temp." : "hcq.") + Math.max((int) Math.ceil(Math.log10(capacity)), MAX_QUEUE_CAPACITY_LOG) + "." + name;
    }
    
    @Override
    public HCQQueue getOrCreateQueue(String name, int capacity, boolean autoDelete)
    {
        Objects.requireNonNull(name, "Queue name cannot be null");
        // add the queue metadata
        QueueMeta meta = this.queueMetadata.computeIfAbsent(name, (k) -> new QueueMeta(computeQueueName(false, name, capacity), autoDelete, false));
        // create the queue
        IQueue<QueuedMessage> queue = this.hazelcastInstance.getQueue(meta.getQueueName());
        return new QueueImpl(meta.toInfo(name), queue);
    }
    
    @Override
    public HCQQueue getOrCreateTempQueue(String name, int capacity, ClientInfo owner)
    {
        Objects.requireNonNull(name, "Queue name cannot be null");
        // add the queue metadata
        QueueMeta meta = this.queueMetadata.computeIfAbsent(name, (k) -> new QueueMeta(computeQueueName(false, name, capacity), false, true));
        if (! meta.isTemporary())
            throw new IllegalArgumentException("A non temporary queue already exists with the name: " + name);
        // insert a claim for the queue
        this.clientTemporaryQueues.put(owner.getId(), name);
        // create the queue
        IQueue<QueuedMessage> queue = this.hazelcastInstance.getQueue(meta.getQueueName());
        return new QueueImpl(meta.toInfo(name), queue);
    }
    
    @Override
    public HCQQueue getQueue(String name)
    {
        Objects.requireNonNull(name, "Queue name cannot be null");
        // add the queue metadata
        QueueMeta meta = this.queueMetadata.get(name);
        if (meta == null) return null;
        // create the queue
        IQueue<QueuedMessage> queue = this.hazelcastInstance.getQueue(meta.getQueueName());
        return new QueueImpl(meta.toInfo(name), queue);
    }
    
    @Override
    public Set<QueueInfo> getQueueInfo()
    {
        Set<QueueInfo> i = new TreeSet<QueueInfo>();
        for (Entry<String, QueueMeta> q : this.queueMetadata.entrySet())
        {
            i.add(q.getValue().toInfo(q.getKey()));
        }
        return i;
    }
    
    @Override
    public QueueInfo getQueueInfo(String name)
    {
        Objects.requireNonNull(name, "Queue name cannot be null");
        QueueMeta meta = this.queueMetadata.get(name);
        return meta == null ? null : meta.toInfo(name);
    }
    
    @Override
    public void destroyQueue(String name)
    {
        Objects.requireNonNull(name, "Queue name cannot be null");
        QueueMeta meta = this.queueMetadata.get(name);
        if (meta != null)
        {
            this.hazelcastInstance.getQueue(meta.getQueueName()).destroy();
            for (String exchange : this.queueToExchangeBindings.get(name))
            {
                for (BindingMeta binding : this.exchangeBindings.get(exchange))
                {
                    if (binding.getTargetName().equals(name))
                        this.exchangeBindings.remove(exchange, binding);
                }
            }
            this.queueToExchangeBindings.remove(name);
            this.queueMetadata.remove(name);
        }
    }
    
    @Override
    public HCQExchange getOrCreateExchange(String name, String type)
    {
        Objects.requireNonNull(name, "Exchange name cannot be null");
        // add the queue metadata
        ExchangeMeta meta = this.exchangeMetadata.computeIfAbsent(name, (k) -> new ExchangeMeta(type));
        // create the exchange
        return new ExchangeImpl(meta.toInfo(name), this);
    }
    
    @Override
    public HCQExchange getExchange(String name)
    {
        Objects.requireNonNull(name, "Exchange name cannot be null");
        // add the queue metadata
        ExchangeMeta meta = this.exchangeMetadata.get(name);
        if (meta == null) return null;
        // create the exchange
        return new ExchangeImpl(meta.toInfo(name), this);
    }
    
    @Override
    public Set<ExchangeInfo> getExchangeInfo()
    {
        Set<ExchangeInfo> i = new TreeSet<ExchangeInfo>();
        for (Entry<String, ExchangeMeta> e : this.exchangeMetadata.entrySet())
        {
            i.add(e.getValue().toInfo(e.getKey()));
        }
        return i;
    }
    
    @Override
    public ExchangeInfo getExchangeInfo(String name)
    {
        Objects.requireNonNull(name, "Exchange name cannot be null");
        ExchangeMeta meta = this.exchangeMetadata.get(name);
        return meta == null ? null : meta.toInfo(name);
    }
    
    @Override
    public void bindQueueToExchange(String exchangeName, String routingKey, String targetName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Objects.requireNonNull(targetName, "Target name cannot be null");
        // get the exchange
        ExchangeMeta exchangeMeta = this.exchangeMetadata.get(exchangeName);
        if (exchangeMeta == null) throw new RuntimeException("Exchange does not exist");
        // get the queue
        QueueMeta queueMeta = this.queueMetadata.get(targetName);
        if (queueMeta == null) throw new RuntimeException("Queue does not exist");
        // add the binding
        this.exchangeBindings.put(exchangeName, BindingMeta.queueBinding(routingKey, targetName));
        this.queueToExchangeBindings.put(targetName, exchangeName);
    }
    
    @Override
    public void bindExchangeToExchange(String exchangeName, String routingKey, String targetName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Objects.requireNonNull(targetName, "Target name cannot be null");
        // get the exchange
        ExchangeMeta exchangeMeta = this.exchangeMetadata.get(exchangeName);
        if (exchangeMeta == null) throw new RuntimeException("Exchange does not exist");
        // get the queue
        ExchangeMeta targetMeta = this.exchangeMetadata.get(targetName);
        if (targetMeta == null) throw new RuntimeException("Exchange does not exist");
        // add the binding
        this.exchangeBindings.put(exchangeName, BindingMeta.exchangeBinding(routingKey, targetName));
        this.exchangeToExchangeBindings.put(targetName, exchangeName);
    }
    
    @Override
    public Set<BindingInfo> getBindingInfo(String exchangeName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Set<BindingInfo> info = new HashSet<BindingInfo>();
        for (BindingMeta meta : this.exchangeBindings.get(exchangeName))
        {
            info.add(meta.toInfo(exchangeName));
        }
        return info;
    }
    
    @Override
    public void unbindQueueToExchange(String exchangeName, String routingKey, String targetName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Objects.requireNonNull(targetName, "Target name cannot be null");
        int targetCount = 0;
        int matchCount  = 0;
        for (BindingMeta binding : this.exchangeBindings.get(exchangeName))
        {
            if (binding.isTargetAQueue() && binding.getTargetName().equals(targetName))
            {
                targetCount++;
                if (binding.getKey() == null || routingKey == null || binding.getKey().equals(routingKey))
                {
                    this.exchangeBindings.remove(exchangeName, binding);
                    matchCount++;
                }
            }
        }
        if (matchCount > 0 && targetCount == matchCount)
            this.queueToExchangeBindings.remove(targetName);
    }
    
    @Override
    public void unbindExchangeToExchange(String exchangeName, String routingKey, String targetName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Objects.requireNonNull(targetName, "Target name cannot be null");
        int targetCount = 0;
        int matchCount  = 0;
        for (BindingMeta binding : this.exchangeBindings.get(exchangeName))
        {
            if (binding.isTargetAnExchange() && binding.getTargetName().equals(targetName))
            {
                targetCount++;
                if (binding.getKey() == null || routingKey == null || binding.getKey().equals(routingKey))
                {
                    this.exchangeBindings.remove(exchangeName, binding);
                    matchCount++;
                }
            }
        }
        if (matchCount > 0 && targetCount == matchCount)
            this.exchangeToExchangeBindings.remove(targetName);
    }
    
    @Override
    public void destroyExchange(String name)
    {
        Objects.requireNonNull(name, "Exchange name cannot be null");
        this.exchangeMetadata.remove(name);
        for (String exchange : this.exchangeToExchangeBindings.get(name))
        {
            for (BindingMeta binding : this.exchangeBindings.get(exchange))
            {
                if (binding.getTargetName().equals(name))
                    this.exchangeBindings.remove(exchange, binding);
            }
        }
        this.exchangeToExchangeBindings.remove(name);
        // remove the alternate
        this.exchangeAlternates.remove(name);
        // remove any alternates
        this.exchangeAlternates.removeAll(new ValueEqualsPredicate<String, String>(name));
    }    
    
    @Override
    public void bindAlternateExchange(String exchangeName, String targetName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        Objects.requireNonNull(targetName, "Target name cannot be null");
        // get the exchange
        ExchangeMeta exchangeMeta = this.exchangeMetadata.get(exchangeName);
        if (exchangeMeta == null) throw new RuntimeException("Exchange does not exist");
        // get the queue
        ExchangeMeta targetMeta = this.exchangeMetadata.get(targetName);
        if (targetMeta == null) throw new RuntimeException("Exchange does not exist");
        // add the alternate
        this.exchangeAlternates.put(exchangeName, targetName);
    }

    @Override
    public void unbindAlternateExchange(String exchangeName)
    {
        Objects.requireNonNull(exchangeName, "Exchange name cannot be null");
        // get the exchange
        ExchangeMeta exchangeMeta = this.exchangeMetadata.get(exchangeName);
        if (exchangeMeta == null) throw new RuntimeException("Exchange does not exist");
        // remove the alternate
        this.exchangeAlternates.remove(exchangeName);
    }
    
    public AlternateExchangeInfo getAlternateExchangeInfo(String exchangeName)
    {
        String alt = this.exchangeAlternates.get(exchangeName);
        return alt == null ? null : new AlternateExchangeInfo(exchangeName, alt);
    }

    // internal
    Collection<BindingMeta> _getExchangeBindings(String exchangeName)
    {
        return this.exchangeBindings.get(exchangeName);
    }
    
    String _getAlternateExchange(String exchangeName)
    {
        return this.exchangeAlternates.get(exchangeName);
    }
    
    // util
    
    @Override
    public void memberAdded(MembershipEvent membershipEvent)
    {
        System.out.println("New member joined the cluster: " + membershipEvent.getMember().getUuid());
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent)
    {
        Member left = membershipEvent.getMember();
        String leftId = left.getUuid();
        System.out.println("A member left the cluster: " + leftId);
        if (this.memberClients.tryLock(leftId))
        {
            try
            {
                System.out.println("Cleaning up member: " + leftId);
                for (String clientId : this.memberClients.get(leftId))
                {
                    this.cleanupClient(leftId, clientId);
                }
                this.memberClients.remove(leftId);
            }
            finally
            {
                this.memberClients.unlock(leftId);
            }
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent)
    {
    }
}
