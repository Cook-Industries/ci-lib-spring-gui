/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 11.07.2019 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public final class Button extends Input
{

    @NonNull
    private String      text;
    @NonNull
    private String      onClick;
    @Default
    private ButtonClass btnClass = ButtonClass.DEFAULT;

    @Override
    protected InputType inferType()
    {
        return InputType.BUTTON;
    }

}
