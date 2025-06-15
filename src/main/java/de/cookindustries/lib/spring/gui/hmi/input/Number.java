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
public final class Number extends SubmittableInput
{

    @NonNull
    private final Integer value;
    @NonNull
    private final Integer min;
    @NonNull
    private final Integer max;
    @NonNull
    private final String  placeholder;
    @NonNull
    private final String  prefix;
    @NonNull
    private final String  suffix;

    @Override
    protected InputType inferType()
    {
        return InputType.NUMBER;
    }

}
