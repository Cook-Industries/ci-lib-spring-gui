/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.exception;

public class JsonMapperException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public JsonMapperException(String msg)
    {
        super(msg);
    }

    public JsonMapperException(Throwable cause)
    {
        super(cause);
    }

    public JsonMapperException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
