package com.intrbiz.hcq.broker;

import java.io.Serializable;

import com.intrbiz.hcq.model.ExchangeInfo;

public class ExchangeMeta implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final long createdAt;
    
    private final String type;
    
    public ExchangeMeta(String type)
    {
        super();
        this.createdAt = System.currentTimeMillis();
        this.type = type;
    }
    
    public long getCreatedAt()
    {
        return this.createdAt;
    }
    
    public ExchangeInfo toInfo(String name)
    {
        return new ExchangeInfo(name, this.createdAt, this.type);
    }
}
