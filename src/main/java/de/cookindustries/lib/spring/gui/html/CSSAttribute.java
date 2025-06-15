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
public class CSSAttribute implements HtmlExportable
{

    final CSSAttributeType type;
    final Object           value;

    public CSSAttribute(CSSAttributeType type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getHtmlRep()
    {
        return String.format("%s: %s;", type.getName(), value.toString());
    }
}
