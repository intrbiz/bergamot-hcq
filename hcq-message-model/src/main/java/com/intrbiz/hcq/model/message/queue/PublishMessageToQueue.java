package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

@JsonTypeName("hcq.queue.publish_message")
public class PublishMessageToQueue extends HCQRequest
{
    @JsonProperty("queue_name")
    private String queueName;
    
    @JsonProperty("message")
    private QueuedMessage message;
    
    public PublishMessageToQueue()
    {
        super();
    }

    public PublishMessageToQueue(String queueName, QueuedMessage message)
    {
        super();
        this.queueName = queueName;
        this.message = message;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public QueuedMessage getMessage()
    {
        return message;
    }

    public void setMessage(QueuedMessage message)
    {
        this.message = message;
    }
}
