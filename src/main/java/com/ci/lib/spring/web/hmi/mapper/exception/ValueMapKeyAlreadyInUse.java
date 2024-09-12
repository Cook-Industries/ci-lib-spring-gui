/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper.exception;

public class ValueMapKeyAlreadyInUse extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ValueMapKeyAlreadyInUse(String key, Class<?> objectClass)
    {
        super(String.format("key [%s] is already in use. source class [%s]", key, objectClass.getSimpleName()));
    }
}
