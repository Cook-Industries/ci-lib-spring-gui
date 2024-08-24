package com.ci.lib.spring.web.html;

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
