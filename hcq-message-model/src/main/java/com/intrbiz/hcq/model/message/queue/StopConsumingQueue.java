package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.queue.stop_consuming")
public class StopConsumingQueue extends HCQRequest
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public StopConsumingQueue()
    {
        super();
    }

    public StopConsumingQueue(String queueName)
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
