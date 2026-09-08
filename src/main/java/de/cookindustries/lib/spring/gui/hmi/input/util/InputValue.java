/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * An object representing various input values for checkboxes, selection and others.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@ToString
public class InputValue extends UiElement
{

    /** The text to this input value */
    private final String  submitAs;

    /** The text to this input value */
    @NonNull
    private final String  text;

    /** The value that gets transmitted at form-export */
    @NonNull
    private final String  value;

    /** Whether this input value is selected or not */
    @Default
    private final boolean checked = false;

}
