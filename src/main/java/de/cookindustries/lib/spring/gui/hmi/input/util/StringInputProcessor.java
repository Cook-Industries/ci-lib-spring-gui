/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link String}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class StringInputProcessor extends AbsInputProcessor<String>
{

    /** Whether or not an empty {@code input} is allowed */
    @Default
    @NonNull
    private final Boolean                  allowEmpty         = true;

    /** Default value if the {@code input} is empty and is not allowed */
    @Default
    private final String                   fallback           = null;

    /** Pattern to match the {@code input} against */
    @Default
    private final Pattern                  pattern            = null;

    /** Set of allowed Strings to match the {@code input} */
    @Singular
    private final Set<String>              accepts;

    /** Set of <b>not</b> allowed Strings to check against the {@code input} */
    @Singular
    private final Set<String>              rejects;

    /** Whether the {@code accept} or {@code reject} sets should checked case sensitive */
    @Default
    @NonNull
    private final Boolean                  caseSensitiveCheck = true;

    @Default
    private final Function<String, String> sanitazation       = null;

    @Override
    protected String parseRaw(String input)
    {
        return input;
    }

    @Override
    protected InputCheckResult<String> check(String input)
    {
        if (input.isEmpty())
        {
            if (allowEmpty)
            {
                return createResult(InputCheckResultType.PASS, input);
            }

            if (fallback != null)
            {
                return createResult(InputCheckResultType.PASS, fallback);
            }

            return createEmptyResult(InputCheckResultType.EMPTY_BUT_EXPECTED);
        }

        if (pattern != null)
        {
            if (pattern.matcher(input).matches())
            {
                return createResult(InputCheckResultType.PASS, input);
            }

            return createEmptyResult(InputCheckResultType.NO_PATTERN_MATCH);
        }

        Predicate<String> predicate =
            caseSensitiveCheck
                ? input::equals
                : input::equalsIgnoreCase;

        if (rejects.stream().anyMatch(predicate))
        {
            return createEmptyResult(InputCheckResultType.REJECTED_VALUE);
        }

        if (!accepts.isEmpty() && !accepts.stream().anyMatch(predicate))
        {
            return createEmptyResult(InputCheckResultType.NOT_ACCEPTED_VALUE);
        }

        return createResult(InputCheckResultType.PASS, input);
    }

    @Override
    protected String prepare(String output)
    {
        if (sanitazation != null)
        {
            return sanitazation.apply(output);
        }

        return output;
    }

}
