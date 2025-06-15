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
