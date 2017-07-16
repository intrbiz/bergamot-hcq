package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.queue.get_or_create")
public class GetOrCreateQueue extends HCQRequest
{
    @JsonProperty("queue_name")
    private String queueName;
    
    @JsonProperty("auto_delete")
    private boolean autoDelete = false;
    
    @JsonProperty("temporary")
    private boolean temporary = false;
    
    @JsonProperty("capacity")
    private int capacity;
    
    public GetOrCreateQueue()
    {
        super();
    }

    public GetOrCreateQueue(String queueName, int capacity, boolean autoDelete)
    {
        super();
        this.queueName = queueName;
        this.capacity = capacity;
        this.autoDelete = autoDelete;
    }
    
    public GetOrCreateQueue(String queueName, int capacity, boolean autoDelete, boolean temporary)
    {
        this(queueName, capacity, autoDelete);
        this.temporary = temporary;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public boolean isAutoDelete()
    {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete)
    {
        this.autoDelete = autoDelete;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    public void setTemporary(boolean temporary)
    {
        this.temporary = temporary;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }
}
