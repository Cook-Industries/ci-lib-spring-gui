/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Textarea extends SubmittableInput
{

    @NonNull
    private final String  value;

    @NonNull
    private final String  placeholder;

    @NonNull
    private final Integer maxCharacters;

    @Override
    protected InputType inferType()
    {
        return InputType.TEXTAREA;
    }

}
