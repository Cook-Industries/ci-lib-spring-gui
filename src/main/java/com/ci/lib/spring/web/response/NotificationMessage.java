/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.response;

import lombok.Data;

@Data
public final class NotificationMessage
{

    private final MessageTarget target;

    private final MessageType   type;

    private final String        msg;
}
