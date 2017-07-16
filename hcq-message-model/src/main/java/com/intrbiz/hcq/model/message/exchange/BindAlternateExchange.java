package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.bind_alternate")
public class BindAlternateExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    @JsonProperty("target_name")
    private String tagetName;
    
    public BindAlternateExchange()
    {
        super();
    }

    public BindAlternateExchange(String exchangeName, String targetName)
    {
        super();
        this.exchangeName = exchangeName;
        this.tagetName = targetName;
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
