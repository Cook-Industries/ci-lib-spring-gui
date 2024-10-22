/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.response;

import java.util.List;

import com.ci.lib.spring.web.function.Call;
import com.ci.lib.spring.web.response.message.ResponseMessage;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class Response
{

    @NonNull
    private final Action              action = inferType();

    @Singular
    private List<ResponseMessage> messages;

    @Singular
    private List<Call>                calls;

    protected abstract Action inferType();

}
