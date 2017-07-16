package com.intrbiz.hcq.broker.router;

import java.util.Collection;

import com.intrbiz.hcq.broker.BindingMeta;

public interface ExchangeRouter
{
    Collection<BindingMeta> route(String key, Collection<BindingMeta> bindings);
}
