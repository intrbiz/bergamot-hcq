package com.intrbiz.hcq.model;

public class AlternateExchangeInfo
{
    private final String exchangeName;

    private final String targetName;

    public AlternateExchangeInfo(String exchangeName, String targetName)
    {
        super();
        this.exchangeName = exchangeName;
        this.targetName = targetName;
    }

    public String getExchangeName()
    {
        return exchangeName;
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
        AlternateExchangeInfo other = (AlternateExchangeInfo) obj;
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
        return this.exchangeName + " [AE] -> " + this.targetName;
    }
}
