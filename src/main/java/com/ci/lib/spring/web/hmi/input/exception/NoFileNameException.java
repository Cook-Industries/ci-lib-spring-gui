package com.ci.lib.spring.web.hmi.input.exception;

public class NoFileNameException extends RuntimeException {

    public NoFileNameException() {
        super("File has no name");
    }
}
