package com.intrbiz.hcq.model;

public class BindingInfo
{
    private final String exchangeName;
    
    private final String binding;
    
    private final String targetType;
    
    private final String targetName;

    public BindingInfo(String exchangeName, String binding, String targetType, String targetName)
    {
        super();
        this.exchangeName = exchangeName;
        this.binding = binding;
        this.targetType = targetType;
        this.targetName = targetName;
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public String getBinding()
    {
        return binding;
    }

    public String getTargetType()
    {
        return targetType;
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
        result = prime * result + ((binding == null) ? 0 : binding.hashCode());
        result = prime * result + ((exchangeName == null) ? 0 : exchangeName.hashCode());
        result = prime * result + ((targetName == null) ? 0 : targetName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BindingInfo other = (BindingInfo) obj;
        if (binding == null)
        {
            if (other.binding != null) return false;
        }
        else if (!binding.equals(other.binding)) return false;
        if (exchangeName == null)
        {
            if (other.exchangeName != null) return false;
        }
        else if (!exchangeName.equals(other.exchangeName)) return false;
        if (targetName == null)
        {
            if (other.targetName != null) return false;
        }
        else if (!targetName.equals(other.targetName)) return false;
        return true;
    }
    
    public String toString()
    {
        return this.exchangeName + " (" + this.binding + ") -> " + this.targetType + "::" + this.targetName;
    }
}
