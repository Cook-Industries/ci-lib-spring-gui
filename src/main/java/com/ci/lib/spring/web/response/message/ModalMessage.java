/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.response.message;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class ModalMessage extends ResponseMessage
{

    private final String msg;

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.MODAL;
    }
}
