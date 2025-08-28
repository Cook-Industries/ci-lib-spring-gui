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
 * Function to open a GUI view relativ to the base url in a new window/tab.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OpenSite extends AbsFunctionCall
{

    public OpenSite(String url)
    {
        this(url, null);
    }

    public OpenSite(String url, AbsFunctionArgs args)
    {
        super();

        setParam(url);
        setParam(args);
    }

    @Override
    protected String functionName()
    {
        return "openSite";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }
}
