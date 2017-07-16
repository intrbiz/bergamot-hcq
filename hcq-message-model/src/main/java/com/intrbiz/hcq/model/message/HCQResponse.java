package com.intrbiz.hcq.model.message;

/**
 * A response to a request from the server
 */
public abstract class HCQResponse extends HCQMessage
{
    public HCQResponse()
    {
        super();
    }

    public HCQResponse(HCQMessage message)
    {
        super(message);
    }
}
