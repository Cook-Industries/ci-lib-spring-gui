/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Set;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Integer}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public final class IntegerInputProcessor extends AbsInputProcessor<Integer>
{

    /** Fallback value if an empty {@code input} is detected and not wanted */
    @Default
    private final Integer      fallback   = null;

    /** Lower bound the {@code input} must clear (inclusive) {@code lowerBound} &lt; {@code input} */
    @NonNull
    @Default
    private final Integer      lowerBound = Integer.MIN_VALUE;

    /** Upper bound the {@code input} must clear (inclusive) {@code input} &lt;= {@code upperBound} */
    @NonNull
    @Default
    private final Integer      upperBound = Integer.MAX_VALUE;

    /** Set of allowed Integers to match the {@code input} */
    @Singular
    private final Set<Integer> accepts;

    /** Set of <b>not</b> allowed Integers to check against the {@code input} */
    @Singular
    private final Set<Integer> rejects;

    @Override
    protected Integer parseRaw(String input)
    {
        if (input.isEmpty())
        {
            if (fallback == null)
            {
                throw new ValueNotPresentException();
            }

            return fallback;
        }

        return Integer.parseInt(input);
    }

    @Override
    protected InputCheckResult<Integer> check(Integer input)
    {
        if (input < lowerBound || input > upperBound)
        {
            return createEmptyResult(InputCheckResultType.OUT_OF_BOUNDS);
        }

        if (rejects.stream().anyMatch(input::equals))
        {
            return createEmptyResult(InputCheckResultType.REJECTED_VALUE);
        }

        if (!accepts.isEmpty() && !accepts.stream().anyMatch(input::equals))
        {
            return createEmptyResult(InputCheckResultType.NOT_ACCEPTED_VALUE);
        }

        return createResult(InputCheckResultType.PASS, input);
    }

}
