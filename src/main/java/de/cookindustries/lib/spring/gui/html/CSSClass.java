/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

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
