package com.intrbiz.hcq.broker.util;

import java.util.Map.Entry;

import com.hazelcast.query.Predicate;

public class ValueEqualsPredicate<K, V> implements Predicate<K,V>
{
    private static final long serialVersionUID = 1L;
    
    private final V value;
    
    public ValueEqualsPredicate(V value)
    {
        this.value = value;
    }

    @Override
    public boolean apply(Entry<K, V> mapEntry)
    {
        return this.value.equals(mapEntry.getValue());
    }
}
