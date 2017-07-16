package com.intrbiz.hcq.model.message.type;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQType;

@JsonTypeName("hcq.queued_message")
public class QueuedMessage extends HCQType
{
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("correlation_id")
    private String correlationId;
    
    @JsonProperty("reply_to")
    private String replyTo;
    
    @JsonProperty("headers")
    private Map<String, String> headers = new HashMap<String, String>();
    
    @JsonProperty("content_type")
    private String contentType;
    
    @JsonProperty("payload")
    private String payload;
    
    @JsonProperty("ttl")
    private int ttl;
    
    public QueuedMessage()
    {
        super();
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getCorrelationId()
    {
        return correlationId;
    }

    public void setCorrelationId(String correlationId)
    {
        this.correlationId = correlationId;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public int getTtl()
    {
        return ttl;
    }

    public void setTtl(int ttl)
    {
        this.ttl = ttl;
    }
    
    public String getReplyTo()
    {
        return replyTo;
    }

    public void setReplyTo(String replyTo)
    {
        this.replyTo = replyTo;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public QueuedMessage randomMessageId()
    {
        this.messageId = UUID.randomUUID().toString();
        return this;
    }
    
    public QueuedMessage randomCorrelationId()
    {
        this.correlationId = UUID.randomUUID().toString();
        return this;
    }
    
    public QueuedMessage messageId(String messageId)
    {
        this.messageId = messageId;
        return this;
    }
    
    public QueuedMessage correlationId(String correlationId)
    {
        this.correlationId = correlationId;
        return this;
    }
    
    public QueuedMessage header(String name, String value)
    {
        this.headers.put(name, value);
        return this;
    }
    
    public QueuedMessage headers(Map<String, String> headers)
    {
        this.headers.putAll(headers);
        return this;
    }
    
    public QueuedMessage ttl(int ttl)
    {
        this.ttl = ttl;
        return this;
    }
    
    public QueuedMessage contentType(String contentType)
    {
        this.contentType = contentType;
        return this;
    }
    
    public QueuedMessage payload(String payload)
    {
        this.payload = payload;
        return this;
    }
    
    public QueuedMessage payload(String contentType, String payload)
    {
        this.contentType = contentType;
        this.payload = payload;
        return this;
    }
    
    public QueuedMessage replyTo(String replyTo)
    {
        this.replyTo = replyTo;
        return this;
    }
}
