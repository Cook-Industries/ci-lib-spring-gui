/**
 * Copyright(c) 2020 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 14.02.2020 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.List;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class SplittedContainer extends Container
{

    @NonNull
    private Container       head;
    @NonNull
    private List<Container> center;
    @NonNull
    private Container       tail;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.SPLITTED;
    }

}
