/**
 * Copyright(c) 2020 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 14.02.2020 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class ContentContainer extends Container
{

    @Singular
    private final List<Container> contents;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.CONTENT;
    }

}
