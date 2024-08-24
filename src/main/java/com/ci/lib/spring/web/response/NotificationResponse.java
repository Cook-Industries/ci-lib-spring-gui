package com.ci.lib.spring.web.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class NotificationResponse extends Response
{

    @Override
    protected Action inferType()
    {
        return Action.NOTIFICATION;
    }

}
