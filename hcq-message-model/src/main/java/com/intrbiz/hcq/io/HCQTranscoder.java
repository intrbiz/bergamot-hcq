package com.intrbiz.hcq.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intrbiz.hcq.model.message.batch.BatchComplete;
import com.intrbiz.hcq.model.message.batch.BatchRequest;
import com.intrbiz.hcq.model.message.error.HCQGeneralError;
import com.intrbiz.hcq.model.message.exchange.BindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BoundExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BoundQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyedExchange;
import com.intrbiz.hcq.model.message.exchange.GetOrCreateExchange;
import com.intrbiz.hcq.model.message.exchange.GotExchange;
import com.intrbiz.hcq.model.message.exchange.PublishMessageToExchange;
import com.intrbiz.hcq.model.message.exchange.PublishedMessageToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnboundQueueToExchange;
import com.intrbiz.hcq.model.message.queue.DestroyQueue;
import com.intrbiz.hcq.model.message.queue.DestroyedQueue;
import com.intrbiz.hcq.model.message.queue.GetOrCreateQueue;
import com.intrbiz.hcq.model.message.queue.GotQueue;
import com.intrbiz.hcq.model.message.queue.PublishMessageToQueue;
import com.intrbiz.hcq.model.message.queue.PublishedMessageToQueue;
import com.intrbiz.hcq.model.message.queue.ReceiveMessageFromQueue;
import com.intrbiz.hcq.model.message.queue.StartConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StartedConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StopConsumingQueue;
import com.intrbiz.hcq.model.message.queue.StoppedConsumingQueue;

/**
 * Encode and decode messages
 */
public class HCQTranscoder
{   
    public static final Class<?>[] CLASSES = {
        // error
        HCQGeneralError.class,
        // queue
        GetOrCreateQueue.class,
        GotQueue.class,
        PublishMessageToQueue.class,
        PublishedMessageToQueue.class,
        StartConsumingQueue.class,
        StartedConsumingQueue.class,
        ReceiveMessageFromQueue.class,
        DestroyQueue.class,
        DestroyedQueue.class,
        StopConsumingQueue.class,
        StoppedConsumingQueue.class,
        // exchange
        GetOrCreateExchange.class,
        GotExchange.class,
        BindQueueToExchange.class,
        BoundQueueToExchange.class,
        BindExchangeToExchange.class,
        BoundExchangeToExchange.class,
        PublishMessageToExchange.class,
        PublishedMessageToExchange.class,
        UnbindQueueToExchange.class,
        UnboundQueueToExchange.class,
        UnbindExchangeToExchange.class,
        UnboundExchangeToExchange.class,
        DestroyExchange.class,
        DestroyedExchange.class,
        BindAlternateExchange.class,
        BoundAlternateExchange.class,
        UnbindAlternateExchange.class,
        UnboundAlternateExchange.class,
        // batch
        BatchRequest.class,
        BatchComplete.class
    };
    
    private static final HCQTranscoder US = new HCQTranscoder();
    
    public static HCQTranscoder getDefaultInstance()
    {
        return US;
    }
    
    private final ObjectMapper factory = new ObjectMapper();
    
    private final boolean sealed;
    
    public HCQTranscoder(boolean sealed)
    {
        super();
        this.factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.factory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.factory.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.factory.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.sealed = sealed;
        this.factory.registerSubtypes(HCQTranscoder.CLASSES);
    }
    
    public HCQTranscoder()
    {
        this(false);
    }
    
    public boolean isSealed()
    {
        return this.sealed;
    }
    
    public void addEventType(Class<?>... classes)
    {
        if (! this.sealed)
        {
            this.factory.registerSubtypes(classes);
        }
    }
    
    public void encode(Object event, OutputStream to)
    {
        try
        {
            JsonGenerator g = this.factory.getFactory().createGenerator(to);
            try
            {
                this.factory.writeValue(g, event);
            }
            finally
            {
                g.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to encode event", e);
        }
    }
    
    public void encode(Object event, Writer to)
    {
        try
        {
            JsonGenerator g = this.factory.getFactory().createGenerator(to);
            try
            {
                this.factory.writeValue(g, event);
            }
            finally
            {
                g.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to encode event", e);
        }
    }
    
    public byte[] encodeAsBytes(Object event)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.encode(event, baos);
        return baos.toByteArray();
    }
    
    public String encodeAsString(Object event)
    {
        StringWriter sw = new StringWriter();
        this.encode(event, sw);
        return sw.toString();
    }
    
    public void encode(Object event, File file)
    {
        file.getParentFile().mkdirs();
        try
        {
            FileWriter fw = new FileWriter(file);
            try
            {
                this.encode(event, fw);
            }
            finally
            {
                fw.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to write file", e);
        }
    }
    
    public <T> T decode(InputStream from, JavaType type)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, type);
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> T decode(InputStream from, Class<T> type)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, type);
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> List<T> decodeList(InputStream from, Class<T> elementType)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, this.factory.getTypeFactory().constructCollectionType(List.class, elementType));
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> Set<T> decodeSet(InputStream from, Class<T> elementType)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, this.factory.getTypeFactory().constructCollectionType(Set.class, elementType));
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
        
    }
    
    public <T> T decode(Reader from, Class<T> type)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return (T) this.factory.readValue(p, type);
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> T decode(Reader from, JavaType type)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, type);
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> List<T> decodeList(Reader from, Class<T> elementType)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, this.factory.getTypeFactory().constructCollectionType(List.class, elementType));
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> Set<T> decodeSet(Reader from, Class<T> elementType)
    {
        try
        {
            JsonParser p = this.factory.getFactory().createParser(from);
            try
            {
                return this.factory.readValue(p, this.factory.getTypeFactory().constructCollectionType(Set.class, elementType));
            }
            finally
            {
                p.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to decode event", e);
        }
    }
    
    public <T> T decodeFromString(String event, Class<T> type)
    {
        return this.decode(new StringReader(event), type);
    }
    
    public <T> T decodeFromString(String event, JavaType type)
    {
        return this.decode(new StringReader(event), type);
    }
    
    public <T> List<T> decodeListFromString(String event, Class<T> elementType)
    {
        return this.decodeList(new StringReader(event), elementType);
    }
    
    public <T> Set<T> decodeSetFromString(String event, Class<T> elementType)
    {
        return this.decodeSet(new StringReader(event), elementType);
    }
    
    public <T> T decodeFromBytes(byte[] event, Class<T> type)
    {
        return this.decode(new ByteArrayInputStream(event), type);
    }
    
    public <T> T decodeFromBytes(byte[] event, JavaType type)
    {
        return this.decode(new ByteArrayInputStream(event), type);
    }
    
    public <T> List<T> decodeListFromBytes(byte[] event, Class<T> elementType)
    {
        return this.decodeList(new ByteArrayInputStream(event), elementType);
    }
    
    public <T> Set<T> decodeSetFromBytes(byte[] event, Class<T> elementType)
    {
        return this.decodeSet(new ByteArrayInputStream(event), elementType);
    }
    
    public <T> T decode(File event, Class<T> type)
    {
        try
        {
            FileReader fr = new FileReader(event);
            try
            {
                return this.decode(fr, type);
            }
            finally
            {
                fr.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read file", e);
        }
    }
    
    public <T> T decode(File event, JavaType type)
    {
        try
        {
            FileReader fr = new FileReader(event);
            try
            {
                return this.decode(fr, type);
            }
            finally
            {
                fr.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read file", e);
        }
    }
    
    public <T> List<T> decodeList(File event, Class<T> elementType)
    {
        try
        {
            FileReader fr = new FileReader(event);
            try
            {
                return this.decodeList(fr, elementType);
            }
            finally
            {
                fr.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read file", e);
        }
    }
    
    public <T> Set<T> decodeSet(File event, Class<T> elementType)
    {
        try
        {
            FileReader fr = new FileReader(event);
            try
            {
                return this.decodeSet(fr, elementType);
            }
            finally
            {
                fr.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
