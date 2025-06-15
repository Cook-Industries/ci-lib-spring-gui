/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
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
public final class Slider extends SubmittableInput
{

    @NonNull
    private final Integer value;
    @NonNull
    private final Integer min;
    @NonNull
    private final Integer max;

    @Override
    protected InputType inferType()
    {
        return InputType.SLIDER;
    }

}
