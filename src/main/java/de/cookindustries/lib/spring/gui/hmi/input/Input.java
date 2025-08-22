/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import lombok.Getter;
import lombok.NonNull;
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
    private final String    onInput = "";

    @NonNull
    private final InputType type    = inferType();

    protected abstract InputType inferType();
}
