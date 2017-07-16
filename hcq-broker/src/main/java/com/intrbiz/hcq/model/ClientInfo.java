package com.intrbiz.hcq.model;

import java.io.Serializable;

public class ClientInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String remoteAddress;

    private String clientUserAgent;

    private String clientApplication;

    private long connected;

    private long lastContact;

    public ClientInfo()
    {
        super();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }

    public String getClientUserAgent()
    {
        return clientUserAgent;
    }

    public void setClientUserAgent(String clientUserAgent)
    {
        this.clientUserAgent = clientUserAgent;
    }

    public String getClientApplication()
    {
        return clientApplication;
    }

    public void setClientApplication(String clientApplication)
    {
        this.clientApplication = clientApplication;
    }

    public long getLastContact()
    {
        return lastContact;
    }

    public long getConnected()
    {
        return connected;
    }

    public void setConnected(long connected)
    {
        this.connected = connected;
    }

    public void setLastContact(long lastContact)
    {
        this.lastContact = lastContact;
    }

    public String toString()
    {
        return "Client #" + this.id + " [" + this.remoteAddress + "] (" + this.clientUserAgent + ") " + this.clientApplication;
    }
}
