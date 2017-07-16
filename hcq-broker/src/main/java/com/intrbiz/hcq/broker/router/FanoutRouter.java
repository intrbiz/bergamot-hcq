package com.intrbiz.hcq.broker.router;

import java.util.Collection;

import com.intrbiz.hcq.broker.BindingMeta;

public class FanoutRouter implements ExchangeRouter
{
    @Override
    public Collection<BindingMeta> route(String key, Collection<BindingMeta> bindings)
    {
        return bindings;
    }
}
