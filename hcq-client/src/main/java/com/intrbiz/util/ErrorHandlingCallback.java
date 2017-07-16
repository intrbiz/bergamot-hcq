package com.intrbiz.util;

import java.util.function.Consumer;

import com.intrbiz.hcq.model.message.HCQResponse;
import com.intrbiz.hcq.model.message.error.HCQError;

public class ErrorHandlingCallback<R extends HCQResponse> implements Consumer<HCQResponse>
{
    private final Consumer<R> onResponse;
    
    private final Consumer<HCQError> onError;

    public ErrorHandlingCallback(Consumer<R> onResponse, Consumer<HCQError> onError)
    {
        super();
        this.onResponse = onResponse;
        this.onError = onError;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(HCQResponse t)
    {
        if (t instanceof HCQError)
        {
            if (this.onError != null) 
                this.onError.accept((HCQError) t);
        }
        else
        {
            this.onResponse.accept((R) t);
        }
    }
}
 