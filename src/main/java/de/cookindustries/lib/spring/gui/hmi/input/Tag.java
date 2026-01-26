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

@SuperBuilder
@Getter
@Jacksonized
public final class Tag extends SubmittableInput
{

    @NonNull
    @Default
    private final String value   = "";

    @Default
    private final String pattern = null;

    @Override
    protected InputType inferType()
    {
        return InputType.TAG;
    }

}
