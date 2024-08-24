package com.ci.lib.spring.web.response;

import lombok.Data;

@Data
public final class NotificationMessage
{

    private final MessageTarget target;

    private final MessageType   type;

    private final String        msg;
}
