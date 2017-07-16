package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.exchange.published_message")
public class PublishedMessageToExchange extends HCQResponse
{
    @JsonProperty("exchange_name")
    private String exchangeName;
    
    public PublishedMessageToExchange()
    {
        super();
    }

    public PublishedMessageToExchange(PublishMessageToExchange request)
    {
        super(request);
        this.exchangeName = request.getExchangeName();
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName)
    {
        this.exchangeName = exchangeName;
    }
}
