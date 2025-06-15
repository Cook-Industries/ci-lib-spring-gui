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
