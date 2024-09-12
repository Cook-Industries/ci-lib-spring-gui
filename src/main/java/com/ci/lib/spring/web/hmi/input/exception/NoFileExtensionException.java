package com.ci.lib.spring.web.hmi.input.exception;

public class NoFileExtensionException extends RuntimeException
{

    public NoFileExtensionException()
    {
        super("File has no extractable extension");
    }
}
