/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 15.11.2019 Author : sebastian koch <koch.sebastian@cook-industries.de>
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
public class ButtonIcon extends Input
{

    @NonNull
    private String      image;
    @NonNull
    private String      onClick;
    @NonNull
    private ButtonClass btnClass;

    @Override
    protected InputType inferType()
    {
        return InputType.BUTTON_ICON;
    }

}
