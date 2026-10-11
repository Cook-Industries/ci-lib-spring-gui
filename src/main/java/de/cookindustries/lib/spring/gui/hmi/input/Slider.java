/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A slider either runs over a numeric range ({@code min}, {@code max}, {@code step}, {@code value} as number) or over a list of
 * {@code customValues} ({@code value} as one of them). Both modes exclude each other, {@code marks} only applies to the numeric one.
 *
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Slider extends SubmittableInput
{

    @NonNull
    private final Serializable     value;

    private final java.lang.Number min;

    private final java.lang.Number max;

    private final java.lang.Number step;

    private final Integer          marks;

    private final List<String>     customValues;

    /**
     * @return {@code true} if the slider runs over {@link #customValues} instead of a numeric range
     */
    public boolean isCustom()
    {
        return customValues != null;
    }

    @Override
    protected InputType inferType()
    {
        return InputType.SLIDER;
    }

}
