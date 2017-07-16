package com.intrbiz.hcq.server.handler.protocol;

import java.util.Arrays;
import java.util.HashSet;

import com.intrbiz.hcq.model.message.HCQRequest;
import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.model.message.batch.BatchComplete;
import com.intrbiz.hcq.model.message.batch.BatchRequest;
import com.intrbiz.hcq.model.message.exchange.BindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.BindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.BindQueueToExchange;
import com.intrbiz.hcq.model.message.exchange.DestroyExchange;
import com.intrbiz.hcq.model.message.exchange.GetOrCreateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindAlternateExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindExchangeToExchange;
import com.intrbiz.hcq.model.message.exchange.UnbindQueueToExchange;
import com.intrbiz.hcq.model.message.queue.DestroyQueue;
import com.intrbiz.hcq.model.message.queue.GetOrCreateQueue;
import com.intrbiz.hcq.server.HCQServerHandler;
import com.intrbiz.hcq.server.handler.ProtocolError;
import com.intrbiz.hcq.server.handler.ProtocolHandler;

public class BatchRequestHandler implements ProtocolHandler<BatchRequest, BatchComplete>
{   
    public static final Class<?>[] PERMITTED_BATCH_REQUEST_TYPES = {
        GetOrCreateQueue.class,
        DestroyQueue.class,
        GetOrCreateExchange.class,
        DestroyExchange.class,
        BindQueueToExchange.class,
        BindExchangeToExchange.class,
        UnbindQueueToExchange.class,
        UnbindExchangeToExchange.class,
        BindAlternateExchange.class,
        UnbindAlternateExchange.class
    };
    
    private final HashSet<Class<?>> permittedBatchRequestTypes = new HashSet<Class<?>>(Arrays.asList(PERMITTED_BATCH_REQUEST_TYPES));
    
    public BatchRequestHandler()
    {
        super();
    }
    
    @Override
    public Class<BatchRequest> getRequestType()
    {
        return BatchRequest.class;
    }

    @Override
    public BatchComplete process(HCQServerHandler context, BatchRequest br) throws Exception
    {
        // validate the requests
        for (HCQRequest request : br.getRequests())
        {
            if (! this.permittedBatchRequestTypes.contains(request.getClass()))
                throw new ProtocolError("The request " + request.getClass().getSimpleName() + " cannot be batched");
        }
        // process the batch
        BatchComplete complete = new BatchComplete(br);
        for (HCQRequest request : br.getRequests())
        {
            // process the request
            HCQResponse response = context.getProtocolProcessor().dispatch(context, request);
            if (response != null) complete.add(response);
        }
        return complete;
    }
}
