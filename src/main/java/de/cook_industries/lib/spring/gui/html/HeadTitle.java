/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
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
