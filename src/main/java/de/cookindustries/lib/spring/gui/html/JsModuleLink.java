/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

/**
 *

 */
public class JsModuleLink implements JsLink
{

    private final String href;

    public JsModuleLink(String href)
    {
        this.href = href;
    }

    @Override
    public String getHtmlRep()
    {
        return String.format("<script type=\"module\" src=\"%s\"></script>", href);
    }
}
