/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import lombok.Getter;
import lombok.Singular;
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
public final class ListSelection extends SubmittableInput
{

    @Default
    private final boolean          multiple = false;

    @Singular
    private final List<InputValue> values;

    @Singular("select")
    private final List<InputValue> selected;

    @Override
    protected InputType inferType()
    {
        return InputType.LIST;
    }

}
