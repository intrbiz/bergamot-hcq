package com.intrbiz.hcq.util;

import java.util.UUID;

public class IdGen
{
    public static String randomId()
    {
        return UUID.randomUUID().toString();
    }
}
