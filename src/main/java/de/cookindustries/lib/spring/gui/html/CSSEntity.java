/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import de.cookindustries.lib.spring.gui.util.StringConcat;

public class CSSEntity implements HtmlExportable
{

    private final String             name;
    private final CSSEntityType      type;
    private final List<CSSAttribute> attributes;

    CSSEntity(String name, CSSEntityType type, List<CSSAttribute> attributes)
    {
        this.name = name;
        this.type = type;
        this.attributes = attributes;
    }

    public static CSSEntiyBuilder entity(String name, CSSEntityType type)
    {
        return new CSSEntiyBuilder(name, type);
    }

    @Override
    public String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl("");
        sc.append(type.getPrefix());
        sc.append(name);
        sc.appendnl(" {");

        attributes.forEach(h -> sc.appendnl(h.getHtmlRep()));

        sc.appendnl("}");

        return sc.getString();
    }

}
