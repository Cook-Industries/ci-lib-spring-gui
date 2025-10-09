/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Boolean}.
 * 
 * @since 2.4.0 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class EnumInputProcessor<T extends Enum<T>> extends AbsInputProcessor<T>
{

    /** Default value if the {@code input} is {@code empty} */
    @Default
    private final T        fallback = null;

    /** Base class to parse enum from */
    @NonNull
    private final Class<T> enumClass;

    @Override
    protected T parseRaw(String input)
    {
        if (input.isEmpty() && fallback != null)
        {
            return fallback;
        }

        return Arrays
            .stream(enumClass.getEnumConstants())
            .filter(
                t -> input
                    .toUpperCase()
                    .replace(" ", "_")
                    .equals(t.name()))
            .findFirst()
            .get();
    }

    @Override
    protected InputCheckResult<T> check(T input)
    {
        return createResult(InputCheckResultType.PASS, input);
    }

}
