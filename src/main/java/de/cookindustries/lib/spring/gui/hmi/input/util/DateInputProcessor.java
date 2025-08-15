/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.sql.Date;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

import lombok.Getter;
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
public class DateInputProcessor extends AbsInputProcessor<Date>
{

    /** Fallback value if an empty {@code input} is detected and not wanted */
    @Default
    private final Date fallback   = null;

    /** Lower bound the {@code input} must clear (inclusive) {@code lowerBound} &gt; {@code input} */
    @Default
    private final Date lowerBound = null;

    /** Upper bound the {@code input} must clear (exclusiv) {@code input} &lt; {@code upperBound} */
    @Default
    private final Date upperBound = null;

    @Override
    protected Date parseRaw(String input)
    {
        if (input.isEmpty())
        {
            if (fallback == null)
            {
                throw new ValueNotPresentException();
            }

            return fallback;
        }

        return Date.valueOf(input);
    }

    @Override
    protected InputCheckResult<Date> check(Date input)
    {
        if ((lowerBound != null && input.before(lowerBound)) || (upperBound != null && input.after(upperBound)))
        {
            return createEmptyResult(InputCheckResultType.OUT_OF_BOUNDS);
        }

        return createResult(InputCheckResultType.PASS, input);
    }

}
