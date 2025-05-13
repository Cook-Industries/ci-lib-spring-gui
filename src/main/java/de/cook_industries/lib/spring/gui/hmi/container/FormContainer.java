/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.container;

import java.util.List;

import de.cook_industries.lib.spring.gui.hmi.input.Input;
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
public final class FormContainer extends Container
{

    @Singular
    private List<Input> inputs;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.FORM;
    }

}
