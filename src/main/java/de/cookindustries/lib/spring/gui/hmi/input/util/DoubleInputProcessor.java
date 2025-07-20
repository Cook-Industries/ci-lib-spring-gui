package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Integer}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public class DoubleInputProcessor extends AbsInputProcessor<Double>
{

    /** Set a maximum number of decimal places for parsing ({@code input} gets rounded up on parsing) */
    @Default
    private final Integer decimalPlaces = null;

    /** Fallback value if an empty {@code input} is detected and not wanted */
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

    @Override
    protected Double parseRaw(String input)
    {
        if (input == null || input.isEmpty())
        {
            if (fallback == null)
            {
                throw new ValueNotPresentException(MarkerType.NOT_PARSABLE);
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
        if (lowerBound < input || input > upperBound)
        {
            return createEmptyResult(InputCheckResultType.OUT_OF_BOUNDS);
        }

        return createEmptyResult(InputCheckResultType.REJECTED_VALUE);
    }

}
