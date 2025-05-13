/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.input;

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
