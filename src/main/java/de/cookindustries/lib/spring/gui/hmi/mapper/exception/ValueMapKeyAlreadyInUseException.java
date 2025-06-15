/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.exception;

public class ValueMapKeyAlreadyInUseException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ValueMapKeyAlreadyInUseException(String key, Class<?> objectClass)
    {
        super(String.format("key [%s] is already in use. source class [%s]", key, objectClass.getSimpleName()));
    }
}
