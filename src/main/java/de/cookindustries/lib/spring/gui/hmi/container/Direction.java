/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

/**
 * Defines the direction in which a {@link Container} should arrange its children (if applicable)
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
