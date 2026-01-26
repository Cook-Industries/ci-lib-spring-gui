/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Textarea extends SubmittableInput
{

    @NonNull
    @Default
    private final String  value         = "";

    @NonNull
    @Default
    private final String  placeholder   = "";

    @Default
    private final Integer maxCharacters = Integer.MAX_VALUE;

    @Default
    private final String  onKeydown     = "";

    @NonNull
    @Default
    private final Integer rows          = 5;

    @Default
    private final Integer cols          = null;

    @Override
    protected InputType inferType()
    {
        return InputType.TEXTAREA;
    }

}
