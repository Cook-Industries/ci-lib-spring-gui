/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class HeadingContainer extends Container
{

    private final String  text;

    @Default
    private final Integer size = 1;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.HEADING;
    }

}
