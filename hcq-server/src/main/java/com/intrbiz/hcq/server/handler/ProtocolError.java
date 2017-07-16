package com.intrbiz.hcq.server.handler;

public class ProtocolError extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProtocolError()
    {
        super();
    }

    public ProtocolError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ProtocolError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProtocolError(String message)
    {
        super(message);
    }

    public ProtocolError(Throwable cause)
    {
        super(cause);
    }
}
