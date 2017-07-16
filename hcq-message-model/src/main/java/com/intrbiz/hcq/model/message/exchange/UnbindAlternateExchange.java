package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.unbind_alternate")
public class UnbindAlternateExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    public UnbindAlternateExchange()
    {
        super();
    }

    public UnbindAlternateExchange(String exchangeName)
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
