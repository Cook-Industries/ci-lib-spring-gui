/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

/**
 * Defines operation modes for JSON mapping in {@link JsonMapper}
 */
public enum TreeHandling {

    /** Defines static transformation without incorporation of value replacement */
    STATIC,

    /** Defines dynamic transformation with incorporation of value replacement */
    DYNAMIC;
}
