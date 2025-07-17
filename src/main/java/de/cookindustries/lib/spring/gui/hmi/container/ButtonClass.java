/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
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
