/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

/**
 *

 */
public class HeadTitle extends HtmlHeadValue
{

    public HeadTitle(String value)
    {
        super(Header.TITLE, value);
    }

    @Override
    String getHtmlCode()
    {
        return String.format("<title>%s</title>", value);
    }
}
