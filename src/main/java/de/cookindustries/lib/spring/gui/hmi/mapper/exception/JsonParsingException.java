/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.exception;

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
        return String
            .format("uid [%s] pos: [%3d:%3d] - [%s] [%s]", uid, depth, element, msg,
                t == null ? "no further error msg" : t.getMessage());
    }
}
