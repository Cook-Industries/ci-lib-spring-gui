/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.input.Input;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a {@code Form} containing several {@link Input} elements
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class FormContainer extends Container
{

    /**
     * list of input children
     */
    @Singular
    private List<Input> inputs;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.FORM;
    }

}
