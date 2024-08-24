/**
 * Copyright(c) 2020 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 14.02.2020 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class HiddenContainer extends Container
{

    @NonNull
    private final Container child;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.HIDDEN;
    }

}
