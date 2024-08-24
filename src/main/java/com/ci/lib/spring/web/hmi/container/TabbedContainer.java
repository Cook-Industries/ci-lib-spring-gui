/**
 * Copyright(c) 2020 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 14.02.2020 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.Set;

import io.micrometer.common.lang.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class TabbedContainer extends Container
{

    @Singular
    private Set<Tab>       tabs;
    @Singular
    private Set<Container> bodies;
    @NonNull
    @Default
    private Boolean        disabled = Boolean.FALSE;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.TAB;
    }

}
