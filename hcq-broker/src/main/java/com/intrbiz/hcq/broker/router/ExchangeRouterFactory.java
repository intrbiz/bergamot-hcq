package com.intrbiz.hcq.broker.router;

import com.intrbiz.hcq.model.HCQExchange;

public class ExchangeRouterFactory
{
    private static final ExchangeRouterFactory US = new ExchangeRouterFactory();
    
    public static final ExchangeRouterFactory get()
    {
        return US;
    }
    
    private static final ExchangeRouter FANOUT = new FanoutRouter();
    
    private static final TopicRouter TOPIC = new TopicRouter();
    
    private ExchangeRouterFactory()
    {
    }
    
    public ExchangeRouter loadRouter(String exchangeType)
    {
        if (exchangeType == null || exchangeType.length() == 0)
            return null;
        // built in
        switch (exchangeType)
        {
            case HCQExchange.TYPE.FANOUT:
                return FANOUT;
            case HCQExchange.TYPE.TOPIC:
                return TOPIC;
        }
        return null;
    }
}
