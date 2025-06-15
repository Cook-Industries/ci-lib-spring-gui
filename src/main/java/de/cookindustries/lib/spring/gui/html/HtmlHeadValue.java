/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
abstract class HtmlHeadValue
{

    protected final Header type;
    protected final String value;

    protected HtmlHeadValue(Header type, String value)
    {
        this.type = type;
        this.value = value;
    }

    abstract String getHtmlCode();
}
