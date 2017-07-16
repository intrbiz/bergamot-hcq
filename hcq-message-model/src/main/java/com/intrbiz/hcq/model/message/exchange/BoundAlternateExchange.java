package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.exchange.bound_alternate")
public class BoundAlternateExchange extends HCQResponse
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    @JsonProperty("target_name")
    private String tagetName;
    
    public BoundAlternateExchange()
    {
        super();
    }

    public BoundAlternateExchange(BindAlternateExchange request)
    {
        super();
        this.exchangeName = request.getExchangeName();
        this.tagetName = request.getTagetName();
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName)
    {
        this.exchangeName = exchangeName;
    }

    public String getTagetName()
    {
        return tagetName;
    }

    public void setTagetName(String tagetName)
    {
        this.tagetName = tagetName;
    }
}
