package com.intrbiz.hcq.model.message;

/**
 * An async callback from the server
 */
public abstract class HCQCallback extends HCQMessage
{
    public HCQCallback()
    {
        super();
    }

    public HCQCallback(HCQMessage message)
    {
        super(message);
    }
    
    public abstract String getRoutingTag();
}
