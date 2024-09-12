/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
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
