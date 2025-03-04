/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import com.ci.lib.spring.web.hmi.input.util.InputValue;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Radio extends SubmittableInput
{

    @Singular
    private final List<InputValue> values;

    @Override
    protected InputType inferType()
    {
        return InputType.RADIO;
    }

}
