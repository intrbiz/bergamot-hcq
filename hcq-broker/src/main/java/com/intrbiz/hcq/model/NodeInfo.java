package com.intrbiz.hcq.model;

public class NodeInfo
{
    private final String id;

    private final String hostName;

    public NodeInfo(String id, String hostName)
    {
        super();
        this.id = id;
        this.hostName = hostName;
    }

    public String getId()
    {
        return id;
    }

    public String getHostName()
    {
        return hostName;
    }
    
    public String toString()
    {
        return this.id + " (" + this.hostName + ")";
    }
}
