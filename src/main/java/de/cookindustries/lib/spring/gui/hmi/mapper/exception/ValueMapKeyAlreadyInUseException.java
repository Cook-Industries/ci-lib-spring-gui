/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.exception;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class ValueMapKeyAlreadyInUseException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ValueMapKeyAlreadyInUseException(String key, Class<?> objectClass)
    {
        super(String.format("key [%s] is already in use. source class [%s]", key, objectClass.getSimpleName()));
    }
}
