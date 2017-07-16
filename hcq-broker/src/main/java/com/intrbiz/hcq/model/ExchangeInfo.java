package com.intrbiz.hcq.model;

public class ExchangeInfo implements Comparable<ExchangeInfo>
{
    private final String name;
    
    private final long createdAt;
    
    private final String type;

    public ExchangeInfo(String name, long createdAt, String type)
    {
        super();
        this.name = name;
        this.createdAt = createdAt;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public long getCreatedAt()
    {
        return createdAt;
    }
    
    public String getType()
    {
        return this.type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ExchangeInfo other = (ExchangeInfo) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public int compareTo(ExchangeInfo o)
    {
        return this.name.compareTo(o.name);
    }
    
    public String toString()
    {
        return "Exchange: " + this.name + ", created: " + this.createdAt + ", type: " + this.type;
    }
}
