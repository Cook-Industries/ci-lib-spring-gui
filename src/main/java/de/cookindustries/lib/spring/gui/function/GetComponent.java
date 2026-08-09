/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Function to load a component from the Backend.
 * 
 * @since 3.2.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetComponent extends AbsFunctionCall
{

    public GetComponent(String url)
    {
        setParam(url);
    }

    @Override
    protected String functionName()
    {
        return "GET";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 1;
    }

}
