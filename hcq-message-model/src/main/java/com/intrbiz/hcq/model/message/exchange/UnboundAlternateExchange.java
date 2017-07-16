package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.exchange.unbound_alternate")
public class UnboundAlternateExchange extends HCQResponse
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    public UnboundAlternateExchange()
    {
        super();
    }

    public UnboundAlternateExchange(UnbindAlternateExchange request)
    {
        super();
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
