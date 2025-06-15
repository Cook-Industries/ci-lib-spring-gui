/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

/**
 *

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
