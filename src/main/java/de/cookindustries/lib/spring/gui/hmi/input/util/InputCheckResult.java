package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Optional;

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

}
