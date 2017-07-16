package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.bind_exchange")
public class BindExchangeToExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("target_name")
    private String tagetName;
    
    public BindExchangeToExchange()
    {
        super();
    }

    public BindExchangeToExchange(String exchangeName, String key, String targetName)
    {
        super();
        this.exchangeName = exchangeName;
        this.key = key;
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

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
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
