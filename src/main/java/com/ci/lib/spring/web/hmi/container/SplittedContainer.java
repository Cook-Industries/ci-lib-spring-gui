/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.List;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class SplittedContainer extends Container
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
