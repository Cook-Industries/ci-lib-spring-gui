package com.ci.lib.spring.web.html;

import java.util.List;

import com.ci.lib.spring.web.util.StringConcat;

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
