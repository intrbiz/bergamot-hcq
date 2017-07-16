package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.destroy")
public class DestroyExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    public DestroyExchange()
    {
        super();
    }

    public DestroyExchange(String exchangeName)
    {
        super();
        this.exchangeName = exchangeName;
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
