package com.ci.lib.spring.web.hmi.mapper.exception;

public class JsonParsingException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public JsonParsingException(String uid, Integer depth, Integer element, String msg)
    {
        super(buildMsg(uid, depth, element, msg));
    }

    public JsonParsingException(String uid, Integer depth, Integer element, String msg, Throwable t)
    {
        super(buildMsg(uid, depth, element, msg), t);
    }

    private static String buildMsg(String uid, Integer depth, Integer element, String msg)
    {
        return String.format("uid [%s] pos: [%3d:%3d] - [%s]", uid, depth, element, msg);
    }
}
