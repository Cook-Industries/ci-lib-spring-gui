package com.ci.lib.spring.web.hmi.mapper.exception;

public class ValueMapSealedException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ValueMapSealedException()
    {
        super("map is already sealed. changes are forbidden");
    }

}
