/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * This is the base class for SVG elements
 * 
 * @since 3.5.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class SVGElement extends UiElement
{

    @NonNull
    @Default
    private final String  style = "";

    @NonNull
    private final SVGType type  = inferType();

    /**
     * Protected function to define the {@link SVGType} of this class for the internal builder
     * 
     * @return the fix {@code type} of the definitive class implementation
     */
    protected abstract SVGType inferType();

}
