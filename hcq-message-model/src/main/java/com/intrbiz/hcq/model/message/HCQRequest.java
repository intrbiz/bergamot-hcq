package com.intrbiz.hcq.model.message;

/**
 * A request to the server
 */
public abstract class HCQRequest extends HCQMessage
{
    public HCQRequest()
    {
        super();
    }

    public HCQRequest(HCQMessage message)
    {
        super(message);
    }
}
