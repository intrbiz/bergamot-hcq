package com.intrbiz.hcq.model.message.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQCallback;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

@JsonTypeName("hcq.queue.receive_message")
public class ReceiveMessageFromQueue extends HCQCallback
{
    @JsonProperty("queue_name")
    private String queueName;
    
    @JsonProperty("message")
    private QueuedMessage message;
    
    public ReceiveMessageFromQueue()
    {
        super();
    }

    public ReceiveMessageFromQueue(String queueName, QueuedMessage message)
    {
        super();
        this.queueName = queueName;
        this.message = message;
    }
    
    public String getRoutingTag()
    {
        return this.queueName;
    }

    public String getQueueName()
    {
        return this.queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public QueuedMessage getMessage()
    {
        return this.message;
    }

    public void setMessage(QueuedMessage message)
    {
        this.message = message;
    }
}
