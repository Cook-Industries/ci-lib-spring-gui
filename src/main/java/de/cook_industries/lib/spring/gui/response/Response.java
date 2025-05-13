/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.response;

import java.util.List;

import de.cook_industries.lib.spring.gui.function.Call;
import de.cook_industries.lib.spring.gui.response.message.ResponseMessage;

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
