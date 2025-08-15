/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Integer}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class DoubleInputProcessor extends AbsInputProcessor<Double>
{

    /** Default value if the {@code input} is {@code empty} */
    @Default
    private final Double  fallback      = null;

    /** Lower bound the {@code input} must clear (inclusive) {@code lowerBound} &lt; {@code input} */
    @NonNull
    @Default
    private final Double  lowerBound    = Double.MIN_VALUE;

    /** Upper bound the {@code input} must clear (exclusiv) {@code input} &lt; {@code upperBound} */
    @NonNull
    @Default
    private final Double  upperBound    = Double.MAX_VALUE;

    /** Set a maximum number of decimal places for parsing ({@code input} gets rounded up on parsing) */
    @Default
    private final Integer decimalPlaces = null;

    @Override
    protected Double parseRaw(String input)
    {
        if (input.isEmpty())
        {
            if (fallback == null)
            {
                throw new ValueNotPresentException();
            }

            return fallback;
        }

        Double value = Double.parseDouble(input);

        if (decimalPlaces != null)
        {
            BigDecimal rounded =
                BigDecimal
                    .valueOf(value)
                    .setScale(decimalPlaces, RoundingMode.HALF_UP);

            value = rounded.doubleValue();
        }

        return value;

    }

    @Override
    protected InputCheckResult<Double> check(Double input)
    {
        if (input < lowerBound || input > upperBound)
        {
            return createEmptyResult(InputCheckResultType.OUT_OF_BOUNDS);
        }

        return createResult(InputCheckResultType.PASS, input);
    }

}
