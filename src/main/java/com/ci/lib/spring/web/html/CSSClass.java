/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
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
