package com.ci.lib.spring.web.util.exception;

public class ObjectSealedException extends RuntimeException
{

    public ObjectSealedException(Class<?> clazz)
    {
        super(String.format("%s is sealed and can not be modified.", clazz.getSimpleName()));
    }
}
