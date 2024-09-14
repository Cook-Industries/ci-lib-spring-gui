/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class ListSelection extends SubmittableInput
{

    @Default
    private final Boolean multiple = false;

    @Singular
    private final List<InputValue> values;

    @Singular("select")
    private final List<InputValue> selected;

    @Override
    protected InputType inferType()
    {
        return InputType.LIST;
    }

}
