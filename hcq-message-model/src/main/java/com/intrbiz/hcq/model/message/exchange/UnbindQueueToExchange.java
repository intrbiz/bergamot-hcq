package com.intrbiz.hcq.model.message.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.exchange.unbind_queue")
public class UnbindQueueToExchange extends HCQRequest
{
    @JsonProperty("exhange_name")
    private String exchangeName;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("target_name")
    private String tagetName;
    
    public UnbindQueueToExchange()
    {
        super();
    }

    public UnbindQueueToExchange(String exchangeName, String key, String targetName)
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
