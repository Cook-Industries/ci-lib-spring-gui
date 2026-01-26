/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
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

    DEFAULT("btn-primary"),

    PRIMARY("btn-primary"),

    SECONDARY("btn-secondary"),

    SUCCESS("btn-success"),

    WARNING("btn-warning"),

    ERROR("btn-danger"),

    DANGER("btn-danger"),

    INFO("btn-info"),

    LIGHT("btn-light"),

    DARK("btn-dark"),

    LINK("btn-link"),

    CUSTOM("btn-custom-color");

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
