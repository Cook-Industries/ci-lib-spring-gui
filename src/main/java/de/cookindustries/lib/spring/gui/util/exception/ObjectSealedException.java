/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.util.exception;

public class ObjectSealedException extends RuntimeException
{

    public ObjectSealedException(Class<?> clazz)
    {
        super(String.format("%s is sealed and can not be modified.", clazz.getSimpleName()));
    }
}
