/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import com.ci.lib.spring.web.hmi.Position;
import com.ci.lib.spring.web.hmi.UiElement;

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
