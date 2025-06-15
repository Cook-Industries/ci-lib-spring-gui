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
    private String  value         = "";
    @NonNull
    @Default
    private String  placeholder   = "";
    @Default
    private String  prefix        = "";
    @Default
    private String  suffix        = "";
    @Default
    private Integer maxCharacters = Integer.MAX_VALUE;
    @Default
    private String  onKeydown     = "";

    @Override
    protected InputType inferType()
    {
        return InputType.TEXTFIELD;
    }

}
