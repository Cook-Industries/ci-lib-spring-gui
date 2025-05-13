/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

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
