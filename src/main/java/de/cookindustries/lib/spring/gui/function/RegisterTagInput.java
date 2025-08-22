/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

/**
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class RegisterTagInput extends AbsFunctionCall
{

    public RegisterTagInput(String uid, String fetchUrl, String searchUrl, boolean enforceWhitelist)
    {
        super();

        setParam(uid);
        setParam(fetchUrl);
        setParam(searchUrl);
        setParam(enforceWhitelist);
    }

    @Override
    protected String functionName()
    {
        return "registerTagInput";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 4;
    }
}
