package com.intrbiz.hcq.model.message.batch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.hcq.model.message.HCQRequest;

@JsonTypeName("hcq.batch.request")
public class BatchRequest extends HCQRequest
{    
    @JsonProperty("requests")
    private List<HCQRequest> requests = new LinkedList<HCQRequest>();
    
    public BatchRequest()
    {
        super();
    }

    public List<HCQRequest> getRequests()
    {
        return requests;
    }

    public void setRequests(List<HCQRequest> requests)
    {
        this.requests = requests;
    }

    public BatchRequest add(HCQRequest request)
    {
        this.requests.add(request);
        return this;
    }
    
    public BatchRequest addAll(Collection<HCQRequest> request)
    {
        this.requests.addAll(request);
        return this;
    }
}
