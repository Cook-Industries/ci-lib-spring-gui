/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Function to register a Tag-input.
 * <p>
 * This function gets assigned automatic when a Tag-input gets created.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterTagInput extends AbsFunctionCall
{

    public RegisterTagInput(TagInputSettings settings)
    {
        super();

        setParam(settings);
    }

    @Override
    protected String functionName()
    {
        return "registerTagInput";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 1;
    }
}
