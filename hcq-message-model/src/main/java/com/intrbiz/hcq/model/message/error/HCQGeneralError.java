package com.intrbiz.hcq.model.message.error;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQMessage;

@JsonTypeName("hcq.error.general")
public class HCQGeneralError extends HCQError
{
    public HCQGeneralError()
    {
        super();
    }
    
    public HCQGeneralError(String message)
    {
        super(message);
    }

    public HCQGeneralError(HCQMessage inResponseTo, String message)
    {
        super(inResponseTo, message);
    }
    
    public HCQGeneralError(String messageId, String message)
    {
        super();
        this.id = messageId;
        this.message = message;
    }
}
