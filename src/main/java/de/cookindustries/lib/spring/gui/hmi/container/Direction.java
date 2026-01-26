/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

/**
 * Defines the direction in which a {@link Container} should arrange its children (if applicable)
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum Direction {

    /** No specified direction */
    NONE,

    /** Standard orientation left-to-right */
    HORIZONTAL,

    /** Standard orientation top-to-bottom */
    VERTICAL,

    /** Inverse standard orientation right-to-left */
    HORIZONTAL_REVERSE,

    /** Inverse standard orientation bottom-to-top */
    VERTICAL_REVERSE;
}
