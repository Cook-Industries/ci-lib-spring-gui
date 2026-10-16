/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

/**
 * Types of elements that only exist as children of a specific container or input.
 * 
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum SubElementType
{

    /** item of a {@code burger} container */
    BURGER_ITEM,

    /** value of a {@code checkbox}, {@code radio} or {@code select} input */
    INPUT_VALUE;

    /**
     * Check whether a template {@code type} denotes this subtype.
     * 
     * @param type from the template
     * @return {@code true} if {@code type} equals the name, ignoring case
     */
    public boolean matches(String type)
    {
        return name().equalsIgnoreCase(type);
    }
}
