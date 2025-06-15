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
