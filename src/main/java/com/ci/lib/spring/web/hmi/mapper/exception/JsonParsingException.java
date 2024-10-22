/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper.exception;

public class JsonParsingException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public JsonParsingException(String uid, Integer depth, Integer element, String msg)
    {
        super(buildMsg(uid, depth, element, msg, null));
    }

    public JsonParsingException(String uid, Integer depth, Integer element, String msg, Throwable t)
    {
        super(buildMsg(uid, depth, element, msg, t), t);
    }

    private static String buildMsg(String uid, Integer depth, Integer element, String msg, Throwable t)
    {
        return String.format("uid [%s] pos: [%3d:%3d] - [%s] [%s]", uid, depth, element, msg, t == null ? "no further error msg": t.getMessage());
    }
}
