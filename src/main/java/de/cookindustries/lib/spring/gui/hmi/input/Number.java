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
    private final String  value;

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

    @NonNull
    @Default
    private final String  onEnterPress = "";

    @Override
    protected InputType inferType()
    {
        return InputType.NUMBER;
    }

}
