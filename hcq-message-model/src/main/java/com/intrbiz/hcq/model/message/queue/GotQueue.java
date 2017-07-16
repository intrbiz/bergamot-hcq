package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.queue.got")
public class GotQueue extends HCQResponse
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public GotQueue()
    {
        super();
    }

    public GotQueue(GetOrCreateQueue request)
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
