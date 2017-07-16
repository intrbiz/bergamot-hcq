package com.intrbiz.hcq.broker;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.intrbiz.hcq.broker.router.ExchangeRouter;
import com.intrbiz.hcq.broker.router.ExchangeRouterFactory;
import com.intrbiz.hcq.model.ExchangeInfo;
import com.intrbiz.hcq.model.HCQExchange;
import com.intrbiz.hcq.model.HCQQueue;
import com.intrbiz.hcq.model.message.type.QueuedMessage;

public class ExchangeImpl implements HCQExchange
{
    private final ExchangeInfo info;
    
    private final HCQBrokerImpl broker;
    
    private final ExchangeRouter router;
    
    public ExchangeImpl(ExchangeInfo info, HCQBrokerImpl broker)
    {
        this.info = info;
        this.broker = broker;
        this.router = ExchangeRouterFactory.get().loadRouter(info.getType());
    }

    @Override
    public ExchangeInfo getInfo()
    {
        return this.info;
    }
    
    private Collection<BindingMeta> applyRouting(String key)
    {
        // get the list of bindings for us
        Collection<BindingMeta> bindings = this.broker._getExchangeBindings(this.info.getName());
        // apply our router
        return this.router == null ? bindings : this.router.route(key, bindings);
    }
    
    private boolean dispatch(String key, QueuedMessage message, QueuePutter queuePutter, ExchangePutter exchangePutter)
    {
        // apply the routing
        Collection<BindingMeta> routes = this.applyRouting(key);
        // dispatch messages via routes
        if (! routes.isEmpty())
        {
            boolean offered = true;
            for (BindingMeta route : routes)
            {
                if (route.isTargetAQueue())
                {
                    HCQQueue queue = this.broker.getQueue(route.getTargetName());
                    if (queue != null)
                    {
                        try
                        {
                            offered = queuePutter.apply(queue, message) || offered;
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                }
                else if (route.isTargetAnExchange())
                {
                    HCQExchange exchange = this.broker.getExchange(route.getTargetName());
                    if (exchange != null)
                    {
                        try
                        {
                            offered = exchangePutter.apply(exchange, message) || offered;
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                }
            }
            return offered;
        }
        // try an alternate exchange
        String alternateExchangeName = this.broker._getAlternateExchange(this.info.getName());
        if (alternateExchangeName != null)
        {
            HCQExchange alternateExchange = this.broker.getExchange(alternateExchangeName);
            if (alternateExchange != null)
            {
                try
                {
                    return exchangePutter.apply(alternateExchange, message);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        return false;
    }

    @Override
    public boolean offer(String key, QueuedMessage message)
    {
        return this.dispatch(key, message, (q, m) -> q.offer(message), (e, m) -> e.offer(key, message));
    }

    @Override
    public boolean offer(String key, QueuedMessage message, long timeout, TimeUnit unit) throws InterruptedException
    {
        return this.dispatch(key, message, (q, m) -> q.offer(message, timeout, unit), (e, m) -> e.offer(key, message));
    }

    @Override
    public boolean put(String key, QueuedMessage message) throws InterruptedException
    {
        return this.dispatch(key, message, (q, m) -> q.put(message), (e, m) -> e.put(key, message));
    }
    
    @FunctionalInterface
    private static interface QueuePutter
    {
        boolean apply(HCQQueue queue, QueuedMessage message) throws InterruptedException;
    }
    
    @FunctionalInterface
    private static interface ExchangePutter
    {
        boolean apply(HCQExchange queue, QueuedMessage message) throws InterruptedException;
    }
}
