package com.intrbiz.hcq.model.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
public abstract class HCQType implements Serializable
{
    private static final long serialVersionUID = 1L;

    public HCQType()
    {
        super();
    }
}
