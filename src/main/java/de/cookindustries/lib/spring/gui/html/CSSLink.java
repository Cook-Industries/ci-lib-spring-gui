/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
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
public class CSSLink implements HtmlExportable
{

    private final String href;

    public CSSLink(String href)
    {
        this.href = href;
    }

    public String getHtmlRep()
    {
        return String.format("<link href=\"%s\" rel=\"stylesheet\">", href);
    }
}
