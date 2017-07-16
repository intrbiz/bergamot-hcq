package com.intrbiz.hcq.model.message.batch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQResponse;

@JsonTypeName("hcq.batch.complete")
public class BatchComplete extends HCQResponse
{
    @JsonProperty("responses")
    private List<HCQResponse> responses = new LinkedList<HCQResponse>();
    
    public BatchComplete()
    {
        super();
    }

    public BatchComplete(BatchRequest request)
    {
        super(request);
    }

    public List<HCQResponse> getResponses()
    {
        return responses;
    }

    public void setResponses(List<HCQResponse> responses)
    {
        this.responses = responses;
    }
    
    public BatchComplete add(HCQResponse response)
    {
        this.responses.add(response);
        return this;
    }
    
    public BatchComplete addAll(Collection<HCQResponse> responses)
    {
        this.responses.addAll(responses);
        return this;
    }
}
