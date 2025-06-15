/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

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
