/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class Slider extends SubmittableInput
{

    @NonNull
    private final Integer value;
    @NonNull
    private final Integer min;
    @NonNull
    private final Integer max;

    @Override
    protected InputType inferType()
    {
        return InputType.SLIDER;
    }

}
