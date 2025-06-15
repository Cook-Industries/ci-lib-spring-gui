/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

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
