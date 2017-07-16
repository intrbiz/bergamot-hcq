package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

@JsonTypeName("hcq.exchange.publish_message")
public class PublishMessageToExchange extends HCQRequest
{
    @JsonProperty("exchange_name")
    private String exchangeName;
    
    @JsonProperty("routing_key")
    private String routingKey;
    
    @JsonProperty("message")
    private QueuedMessage message;
    
    public PublishMessageToExchange()
    {
        super();
    }

    public PublishMessageToExchange(String exchangeName, String routingKey, QueuedMessage message)
    {
        super();
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.message = message;
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName)
    {
        this.exchangeName = exchangeName;
    }

    public QueuedMessage getMessage()
    {
        return message;
    }

    public void setMessage(QueuedMessage message)
    {
        this.message = message;
    }

    public String getRoutingKey()
    {
        return routingKey;
    }

    public void setRoutingKey(String routingKey)
    {
        this.routingKey = routingKey;
    }
}
