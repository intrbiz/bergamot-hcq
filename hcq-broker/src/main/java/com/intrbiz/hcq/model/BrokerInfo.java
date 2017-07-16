package com.intrbiz.hcq.model;

import java.util.List;

public class BrokerInfo
{
    private final String name;

    private final String server;

    private final boolean quorum;

    private final List<NodeInfo> nodes;

    public BrokerInfo(String name, String server, boolean quorum, List<NodeInfo> nodes)
    {
        super();
        this.name = name;
        this.server = server;
        this.quorum = quorum;
        this.nodes = nodes;
    }

    public boolean isQuorum()
    {
        return quorum;
    }

    public List<NodeInfo> getNodes()
    {
        return nodes;
    }

    public String getName()
    {
        return name;
    }

    public String getServer()
    {
        return server;
    }
    
    public String toString()
    {
        return this.name + " (" + this.server + ") quorum=" + this.quorum + "; nodes: " + this.nodes; 
    }
}
