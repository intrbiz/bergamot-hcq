package com.intrbiz.hcq.broker.router;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.intrbiz.hcq.broker.BindingMeta;

public class TopicRouter implements ExchangeRouter
{
    @Override
    public Collection<BindingMeta> route(String key, Collection<BindingMeta> bindings)
    {
        Set<BindingMeta> routes = new HashSet<BindingMeta>();
        // ensure the key is not null
        if (key == null) key = "";
        // apply the routing
        String[] keyParts = null;
        for (BindingMeta binding : bindings)
        {
            String bindingKey = binding.getKey();
            // does the binding accept anything
            if (bindingKey == null || bindingKey.length() == 0 || "#".equals(bindingKey))
            {
                routes.add(binding);
            }
            else
            {
                // does the binding match the key
                if (keyParts == null) keyParts = dotSplit(key);
                String[] bindingKeyParts = binding.getKeyParts();
                boolean matched = true;
                for (int i = 0; i < bindingKeyParts.length || i < keyParts.length; i++)
                {
                    String bindingKeyPart = i < bindingKeyParts.length ? bindingKeyParts[i] : null;
                    String keyPart = i < keyParts.length ? keyParts[i] : null;
                    // wild cards
                    if ("#".equals(bindingKeyPart) && keyParts.length >= bindingKeyParts.length)
                    {
                        break;
                    }
                    if ("*".equals(bindingKeyPart) && keyPart != null  && keyPart.length() > 0)
                    {
                        continue;
                    }
                    if ("**".equals(bindingKeyPart) && keyPart != null)
                    {
                        continue;
                    }
                    // match the parts
                    matched = matched && bindingKeyPart != null && bindingKeyPart.equals(keyPart);
                }
                if (matched)
                {
                    routes.add(binding);
                }
            }
        }
        return routes;
    }
    
    public static String[] dotSplit(String input)
    {
        if (input == null) return null;
        List<String> parts = new LinkedList<String>();
        StringBuilder part = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            if (c == '.')
            {
                parts.add(part.toString());
                part = new StringBuilder();
            }
            else
            {
                part.append(c);
            }
        }
        parts.add(part.toString());
        return parts.toArray(new String[0]);
    }
}
