/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class Input extends UiElement
{

    @NonNull
    @Default
    private String             onInput = "";

    @Singular("marker")
    private final List<Marker> marker;

    @NonNull
    private final InputType    type    = inferType();

    protected abstract InputType inferType();
}
