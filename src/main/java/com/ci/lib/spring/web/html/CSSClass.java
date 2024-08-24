package com.ci.lib.spring.web.html;

public enum CSSClass {

    HIDDEN("hidden");

    private final String className;

    private CSSClass(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
