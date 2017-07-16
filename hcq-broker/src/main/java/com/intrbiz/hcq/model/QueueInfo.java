package com.intrbiz.hcq.model;

public class QueueInfo implements Comparable<QueueInfo>
{
    private final String name;

    private final long createdAt;

    private final boolean autoDelete;

    private final boolean temporary;

    public QueueInfo(String name, long createdAt, boolean autoDelete, boolean temporary)
    {
        super();
        this.name = name;
        this.createdAt = createdAt;
        this.autoDelete = autoDelete;
        this.temporary = temporary;
    }

    public String getName()
    {
        return name;
    }

    public long getCreatedAt()
    {
        return createdAt;
    }

    public boolean isAutoDelete()
    {
        return autoDelete;
    }

    public boolean isTemporary()
    {
        return temporary;
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
        QueueInfo other = (QueueInfo) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public int compareTo(QueueInfo o)
    {
        return this.name.compareTo(o.name);
    }

    public String toString()
    {
        return "Queue: " + this.name + ", created: " + this.createdAt;
    }
}
