/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

/**
 *

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
