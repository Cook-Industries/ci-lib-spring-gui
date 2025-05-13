/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.container;

import de.cook_industries.lib.spring.gui.hmi.Position;
import de.cook_industries.lib.spring.gui.hmi.UiElement;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public abstract class Container extends UiElement
{

    @NonNull
    @Default
    private Direction           direction       = Direction.NONE;
    @NonNull
    @Default
    private String              onClick         = "";
    @NonNull
    @Default
    private String              tooltip         = "";
    @NonNull
    @Default
    private Position            tooltipPosition = Position.BOTTOM;

    @NonNull
    private final ContainerType type            = inferType();

    protected abstract ContainerType inferType();
}
