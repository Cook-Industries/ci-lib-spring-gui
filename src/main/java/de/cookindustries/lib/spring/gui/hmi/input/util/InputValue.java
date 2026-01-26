/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.Builder.Default;

/**
 * An object representing various input values for checkboxes, selection and others.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@ToString
public class InputValue
{

    /**
     * the text to this input value
     */
    @NonNull
    @Default
    private final String       id      = "";

    /**
     * the text to this input value
     */
    @NonNull
    private final String       text;

    /**
     * the value that gets transmitted at form-export
     */
    @NonNull
    private final String       value;

    /**
     * whether this input value is selected or not
     */
    @NonNull
    @Default
    private final Boolean      checked = false;

    /**
     * styling classes for this input value
     */
    @Singular("clazz")
    private final List<String> classes;

}
