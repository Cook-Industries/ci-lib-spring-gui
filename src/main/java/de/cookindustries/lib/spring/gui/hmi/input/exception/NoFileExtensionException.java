/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input.exception;

public class NoFileExtensionException extends RuntimeException
{

    public NoFileExtensionException()
    {
        super("File has no extractable extension");
    }
}
