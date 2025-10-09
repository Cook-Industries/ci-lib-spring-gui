/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * {@link Textfield} represents a text input field.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Textfield extends SubmittableInput
{

    @NonNull
    @Default
    private final String  value         = "";

    @NonNull
    @Default
    private final String  placeholder   = "";

    @Default
    private final String  prefix        = "";

    @Default
    private final String  suffix        = "";

    @Default
    private final Integer maxCharacters = Integer.MAX_VALUE;

    @Default
    private final String  onKeydown     = "";

    @NonNull
    @Default
    private final String  onEnterPress  = "";

    @Override
    protected InputType inferType()
    {
        return InputType.TEXTFIELD;
    }

}
