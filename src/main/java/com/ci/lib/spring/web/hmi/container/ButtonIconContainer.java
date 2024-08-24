/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 11.07.2019 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import com.ci.lib.spring.web.hmi.input.ButtonIcon;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class ButtonIconContainer extends Container
{

    @NonNull
    private ButtonIcon button;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON;
    }

}
