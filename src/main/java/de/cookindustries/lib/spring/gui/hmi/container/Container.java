/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * This is the base class for Container elements
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
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
    private final Direction     direction = Direction.NONE;

    @NonNull
    private final ContainerType type      = inferType();

    /**
     * Protected function to define the {@link ContainerType} of this class for the internal builder
     * 
     * @return the fix {@code type} of the definitive class implementation
     */
    protected abstract ContainerType inferType();
}
