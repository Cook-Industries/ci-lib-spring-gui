/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
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
public class LinkContainer extends Container
{

    @NonNull
    private final String    href;
    @NonNull
    private final String    target;
    @NonNull
    private final Container content;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.LINK;
    }

}
