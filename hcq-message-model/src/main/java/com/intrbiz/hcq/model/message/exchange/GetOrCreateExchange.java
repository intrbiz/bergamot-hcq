package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.get_or_create")
public class GetOrCreateExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    @JsonProperty("exhange_type")
    private String exchangeType;
    
    public GetOrCreateExchange()
    {
        super();
    }

    public GetOrCreateExchange(String exchangeName, String exchangeType)
    {
        super();
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName)
    {
        this.exchangeName = exchangeName;
    }

    public String getExchangeType()
    {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType)
    {
        this.exchangeType = exchangeType;
    }
}
