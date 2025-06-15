/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import de.cookindustries.lib.spring.gui.hmi.input.util.Marker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *

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
