/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Number extends SubmittableInput
{

    @NonNull
    private final java.lang.Number value;

    @NonNull
    private final java.lang.Number min;

    @NonNull
    private final java.lang.Number max;

    @NonNull
    private final java.lang.Number step;

    @NonNull
    private final String           placeholder;

    @NonNull
    @Default
    private final String           onEnterPress = "";

    @Override
    protected InputType inferType()
    {
        return InputType.NUMBER;
    }

}
