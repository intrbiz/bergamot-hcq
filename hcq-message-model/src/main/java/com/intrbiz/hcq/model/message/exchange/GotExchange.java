package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.exchange.got")
public class GotExchange extends HCQResponse
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    public GotExchange()
    {
        super();
    }

    public GotExchange(GetOrCreateExchange request)
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
