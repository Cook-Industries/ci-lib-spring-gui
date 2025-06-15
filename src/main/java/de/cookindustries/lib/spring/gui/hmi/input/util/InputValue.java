/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * An object representing various input values for checkboxes, selection and others.
 */
@Builder
@Getter
public class InputValue
{

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
    private Boolean            checked = false;

    /**
     * styling classes for this input value
     */
    @Singular("clazz")
    private final List<String> classes;

}
