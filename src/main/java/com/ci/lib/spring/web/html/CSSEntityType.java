package com.ci.lib.spring.web.html;

public enum CSSEntityType {

    CLASS("."), TAG("#");

    private String prefix;

    private CSSEntityType(String prefix)
    {
        this.prefix = prefix;
    }

    public final String getPrefix()
    {
        return prefix;
    }
}
