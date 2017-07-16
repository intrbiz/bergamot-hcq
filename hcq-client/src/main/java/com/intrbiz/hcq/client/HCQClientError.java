package com.intrbiz.hcq.client;

import com.intrbiz.hcq.model.message.error.HCQError;

public class HCQClientError extends Exception
{
    private static final long serialVersionUID = 1L;
    
    private final HCQError error;
    
    public HCQClientError(HCQError error)
    {
        super(error.getMessage());
        this.error = error;
    }
    
    public HCQError getError()
    {
        return this.error;
    }
}
