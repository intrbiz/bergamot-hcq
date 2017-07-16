package com.intrbiz.hcq.broker;

import java.io.Serializable;

import com.intrbiz.hcq.broker.router.TopicRouter;
import com.intrbiz.hcq.model.BindingInfo;

public class BindingMeta implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final class TARGET
    {
        public static final char QUEUE = 'Q';

        public static final char EXCHANGE = 'E';
    }

    private final String key;
    
    private final String[] keyParts;

    private final char targetType;

    private final String targetName;

    public BindingMeta(String key, char targetType, String targetName)
    {
        super();
        this.key = key;
        this.keyParts = TopicRouter.dotSplit(key);
        this.targetType = targetType;
        this.targetName = targetName;
    }

    public String getKey()
    {
        return key;
    }
    
    public String[] getKeyParts()
    {
        return this.keyParts;
    }

    public boolean isRouted()
    {
        return this.key != null && this.key.length() != 0;
    }

    public char getTargetType()
    {
        return targetType;
    }

    public boolean isTargetAQueue()
    {
        return this.targetType == TARGET.QUEUE;
    }

    public boolean isTargetAnExchange()
    {
        return this.targetType == TARGET.EXCHANGE;
    }

    public String getTargetName()
    {
        return targetName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((targetName == null) ? 0 : targetName.hashCode());
        result = prime * result + targetType;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BindingMeta other = (BindingMeta) obj;
        if (key == null)
        {
            if (other.key != null) return false;
        }
        else if (!key.equals(other.key)) return false;
        if (targetName == null)
        {
            if (other.targetName != null) return false;
        }
        else if (!targetName.equals(other.targetName)) return false;
        if (targetType != other.targetType) return false;
        return true;
    }

    public static final BindingMeta queueBinding(String key, String targetName)
    {
        return new BindingMeta(key, TARGET.QUEUE, targetName);
    }

    public static final BindingMeta exchangeBinding(String key, String targetName)
    {
        return new BindingMeta(key, TARGET.EXCHANGE, targetName);
    }
    
    public String toString()
    {
        return this.key + " -> " + this.targetType + "::" + this.targetName;
    }
    
    public BindingInfo toInfo(String exchangeName)
    {
        return new BindingInfo(exchangeName, this.key, this.targetType == TARGET.QUEUE ? "queue" : "exchange", this.targetName);
    }
}
