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
public class ResourceLoaderException extends RuntimeException
{

    public ResourceLoaderException(String path, Throwable cause)
    {
        super(String.format("could not load resource [%s]", path), cause);
    }

    public ResourceLoaderException(String path, Class<?> target, Throwable cause)
    {
        super(String.format("could not transform resource [%s] to [%s]", path, target.getSimpleName()), cause);
    }
}
