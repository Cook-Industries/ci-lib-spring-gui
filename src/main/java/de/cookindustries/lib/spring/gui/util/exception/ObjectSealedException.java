/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.exception;

/*
 * +
 * 
 * 
 * @since 1.0.0
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class ObjectSealedException extends RuntimeException
{

    public ObjectSealedException(Class<?> clazz)
    {
        super(String.format("%s is sealed and can not be modified.", clazz.getSimpleName()));
    }
}
