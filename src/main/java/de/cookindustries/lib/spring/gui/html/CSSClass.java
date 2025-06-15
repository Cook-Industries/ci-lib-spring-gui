/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
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
