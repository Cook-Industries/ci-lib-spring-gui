/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input.exception;

public class NoFileExtensionException extends RuntimeException
{

    public NoFileExtensionException()
    {
        super("File has no extractable extension");
    }
}
