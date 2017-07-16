package com.intrbiz.hcq.broker;

import java.io.Serializable;

import com.intrbiz.hcq.model.QueueInfo;

public class QueueMeta implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private final long createdAt;
    
    private boolean autoDelete = false;
    
    private boolean temporary = false;
    
    private final String queueName;
    
    public QueueMeta(String queueName, boolean autoDelete, boolean temporary)
    {
        super();
        this.createdAt = System.currentTimeMillis();
        this.queueName = queueName;
        this.autoDelete = autoDelete;
        this.temporary = temporary;
    }
    
    public long getCreatedAt()
    {
        return this.createdAt;
    }
    
    public String getQueueName()
    {
        return this.queueName;
    }
    
    public QueueInfo toInfo(String name)
    {
        return new QueueInfo(name, this.createdAt, this.autoDelete, this.temporary);
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
}
