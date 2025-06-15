/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

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
