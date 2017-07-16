package com.intrbiz.hcq.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.hcq.io.HCQTranscoder;
import com.intrbiz.hcq.util.IdGen;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
public abstract class HCQMessage
{
    @JsonProperty("id")
    protected String id;
    
    public HCQMessage()
    {
        super();
        this.id = IdGen.randomId();
    }
    
    public HCQMessage(HCQMessage message)
    {
        super();
        this.id = message.getId();
    }

    public final String getId()
    {
        return id;
    }

    public final void setId(String id)
    {
        this.id = id;
    }
    
    public String toString()
    {
        return HCQTranscoder.getDefaultInstance().encodeAsString(this);
    }
}
