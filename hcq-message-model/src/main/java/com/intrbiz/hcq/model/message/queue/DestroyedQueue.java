package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.queue.destroyed")
public class DestroyedQueue extends HCQResponse
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public DestroyedQueue()
    {
        super();
    }

    public DestroyedQueue(DestroyQueue request)
    {
        super(request);
        this.queueName = request.getQueueName();
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
