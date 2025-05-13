/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cook_industries.lib.spring.gui.hmi.input.util.InputValue;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Select extends SubmittableInput
{

    @Singular
    private final List<InputValue> values;

    @Override
    protected InputType inferType()
    {
        return InputType.SELECT;
    }

}
