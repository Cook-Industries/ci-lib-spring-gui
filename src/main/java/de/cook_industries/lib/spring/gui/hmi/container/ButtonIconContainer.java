/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.container;

import de.cook_industries.lib.spring.gui.hmi.input.ButtonIcon;

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
public final class ButtonIconContainer extends Container
{

    @NonNull
    private ButtonIcon button;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON;
    }

}
