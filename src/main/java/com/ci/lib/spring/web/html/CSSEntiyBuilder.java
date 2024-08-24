package com.ci.lib.spring.web.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSSEntiyBuilder
{

    private final String             name;
    private final CSSEntityType      type;
    private final List<CSSAttribute> attributes;

    CSSEntiyBuilder(String name, CSSEntityType type)
    {
        this.name = name;
        this.type = type;
        this.attributes = new ArrayList<>();
    }

    public CSSEntiyBuilder attribute(CSSAttribute attribute)
    {
        attributes.add(attribute);

        return this;
    }

    public final CSSEntity build()
    {
        return new CSSEntity(name, type, Collections.unmodifiableList(attributes));
    }
}
