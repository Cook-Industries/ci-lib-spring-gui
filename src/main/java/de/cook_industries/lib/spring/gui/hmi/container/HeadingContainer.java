/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.container;

import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class HeadingContainer extends Container
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
