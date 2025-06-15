/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
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
