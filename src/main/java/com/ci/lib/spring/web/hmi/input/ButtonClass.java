package com.ci.lib.spring.web.hmi.input;

public enum ButtonClass {

    DEFAULT("btn-primary"), SUCCESS("btn-success");

    private final String className;

    private ButtonClass(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
