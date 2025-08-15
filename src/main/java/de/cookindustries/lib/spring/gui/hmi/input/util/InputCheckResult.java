/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
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

    public T getResult()
    {
        return result.orElseThrow(() -> new ValueNotPresentException());
    }
}
