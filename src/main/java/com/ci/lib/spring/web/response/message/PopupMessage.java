package com.ci.lib.spring.web.response.message;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PopupMessage extends ResponseMessage
{

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.POP_UP;
    }

}
