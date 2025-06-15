/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input.exception;

public class NoFileNameException extends RuntimeException
{

    public NoFileNameException()
    {
        super("File has no name");
    }
}
