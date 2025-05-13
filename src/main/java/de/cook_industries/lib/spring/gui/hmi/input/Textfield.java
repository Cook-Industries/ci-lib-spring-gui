/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * {@link Textfield} represents a text input field.<br>
 * <br>
 * Required fields are: {@link Textfield#uid}, {@link Textfield#name}, {@link Textfield#submitAs},
 * {@link Textfield#value}, {@link Textfield#placeholder},
 * 
 * @author sebastian koch <koch.sebastian@cook-industries.de>
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
