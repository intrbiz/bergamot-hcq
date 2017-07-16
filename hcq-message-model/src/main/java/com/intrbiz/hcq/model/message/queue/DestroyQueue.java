package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.queue.destroy")
public class DestroyQueue extends HCQRequest
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public DestroyQueue()
    {
        super();
    }

    public DestroyQueue(String queueName)
    {
        super();
        this.queueName = queueName;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }
}
