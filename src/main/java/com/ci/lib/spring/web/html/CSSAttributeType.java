package com.ci.lib.spring.web.html;

public enum CSSAttributeType {

    COLOR("color"), FONT_SIZE("font-size");

    private String name;

    private CSSAttributeType(String name)
    {
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }
}
