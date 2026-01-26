/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Currency extends SubmittableInput
{

    @NonNull
    private final Integer valueF;

    @NonNull
    private final Integer valueB;

    @NonNull
    private final String  symbol;

    @NonNull
    @Default
    private final Integer min         = Integer.MIN_VALUE;

    @NonNull
    @Default
    private final Integer max         = Integer.MAX_VALUE;

    @NonNull
    @Default
    private String        placeholder = "0.0";

    @Override
    protected InputType inferType()
    {
        return InputType.CURRENCY;
    }

}
