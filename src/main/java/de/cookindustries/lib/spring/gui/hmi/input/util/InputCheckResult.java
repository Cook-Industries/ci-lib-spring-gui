/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Optional;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public final class InputCheckResult<T>
{

    @NonNull
    private final InputCheckResultType type;

    @NonNull
    private final Optional<T>          result;

    @Default
    private final Object[]             translationPlaceholderValues = new Object[0];

    /**
     * Get the result itself.
     * 
     * @return the result
     * @throws ValueNotPresentException if no result was set
     */
    public T getResult()
    {
        return result.orElseThrow(ValueNotPresentException::new);
    }
}
