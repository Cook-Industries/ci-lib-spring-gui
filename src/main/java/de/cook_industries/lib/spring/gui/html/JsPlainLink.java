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
public class JsPlainLink implements JsLink
{

    private final String href;

    public JsPlainLink(String href)
    {
        this.href = href;
    }

    @Override
    public String getHtmlRep()
    {
        return String.format("<script src=\"%s\"></script>", href);
    }
}
