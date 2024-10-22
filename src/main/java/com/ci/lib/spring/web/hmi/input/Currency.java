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
public class Currency extends SubmittableInput
{

    @NonNull
    private Integer valueF;
    @NonNull
    private Integer valueB;
    @NonNull
    private String  symbol;
    @NonNull
    private Integer min;
    @NonNull
    private Integer max;
    @NonNull
    private String  placeholder;

    @Override
    protected InputType inferType()
    {
        return InputType.CURRENCY;
    }

}
