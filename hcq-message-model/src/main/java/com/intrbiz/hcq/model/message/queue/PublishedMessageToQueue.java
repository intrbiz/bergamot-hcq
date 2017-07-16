package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.queue.published_message")
public class PublishedMessageToQueue extends HCQResponse
{
    @JsonProperty("queue_name")
    private String queueName;
    
    public PublishedMessageToQueue()
    {
        super();
    }

    public PublishedMessageToQueue(PublishMessageToQueue request)
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
