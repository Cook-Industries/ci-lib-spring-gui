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
class CSSLink
{

    private final String href;

    public CSSLink(String href)
    {
        this.href = href;
    }

    String getHtmlCode()
    {
        return String.format("<link href=\"%s\" rel=\"stylesheet\">", href);
    }
}
