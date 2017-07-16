package com.intrbiz.hcq.model.message.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrbiz.hcq.model.message.HCQMessage;
import com.intrbiz.hcq.model.message.HCQResponse;

public abstract class HCQError extends HCQResponse
{
    @JsonProperty("message")
    protected String message;
    
    public HCQError()
    {
        super();
    }
    
    public HCQError(String message)
    {
        super();
        this.message = message;
    }
    
    public HCQError(HCQMessage inResponseTo, String message)
    {
        super(inResponseTo);
        this.message = message;
    }

    public final String getMessage()
    {
        return message;
    }

    public final void setMessage(String message)
    {
        this.message = message;
    }
}
