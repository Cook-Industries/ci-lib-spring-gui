/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.TagInputSettings;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
public final class Tag extends SubmittableInput
{

    @NonNull
    @Singular
    private final List<String>     tags;

    @NonNull
    @Default
    private final String           pattern = "#?[a-zA-Z]{1}[a-zA-Z0-9\\-]{2,*}";

    @NonNull
    private final TagInputSettings settings;

    @Override
    protected InputType inferType()
    {
        return InputType.TAG;
    }

}
