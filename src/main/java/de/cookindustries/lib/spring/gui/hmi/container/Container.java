/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import de.cookindustries.lib.spring.gui.hmi.Position;
import de.cookindustries.lib.spring.gui.hmi.UiElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * This is the base class for Container elements
 */
@SuperBuilder
@Getter
public abstract class Container extends UiElement
{

    /**
     * the direction of this {@code Container}, if applicable
     */
    @NonNull
    @Default
    private Direction           direction       = Direction.NONE;

    /**
     * a function to call when this {@code Container} is triggerd by a onClick-event
     */
    @NonNull
    @Default
    private String              onClick         = "";

    /**
     * a text for a {@code tooltip} to show on this element triggerd by a onHover-event
     */
    @NonNull
    @Default
    private String              tooltip         = "";

    /**
     * prefered position of {@code tooltip} in relation to the outer bounding of this {@code Container}
     */
    @NonNull
    @Default
    private Position            tooltipPosition = Position.BOTTOM;

    @NonNull
    private final ContainerType type            = inferType();

    /**
     * Protected function to define the {@link ContainerType} of this class for the internal builder
     * 
     * @return the fix {@code type} of the definitive class implementation
     */
    protected abstract ContainerType inferType();
}
