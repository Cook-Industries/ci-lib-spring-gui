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
public class ColumnContainer extends Container
{

    @NonNull
    private final Container content;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.COLUMN;
    }

}
