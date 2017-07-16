package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.queue.start_consuming")
public class StartConsumingQueue extends HCQRequest
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public StartConsumingQueue()
    {
        super();
    }

    public StartConsumingQueue(String queueName)
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
