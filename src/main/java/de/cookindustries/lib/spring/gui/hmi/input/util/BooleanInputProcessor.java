package de.cookindustries.lib.spring.gui.hmi.input.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Boolean}.
 * 
 * @since 2.4.0 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public final class BooleanInputProcessor extends AbsInputProcessor<Boolean>
{

    /** Default value if the {@code input} is {@code null} */
    @NonNull
    @Default
    private final Boolean fallback = false;

    @Override
    protected Boolean parseRaw(String input)
    {
        return input == null || input.isEmpty()
            ? fallback
            : Boolean.parseBoolean(input.toLowerCase());
    }

    @Override
    protected InputCheckResult<Boolean> check(Boolean input)
    {
        return createResult(InputCheckResultType.PASS, input);
    }

}
