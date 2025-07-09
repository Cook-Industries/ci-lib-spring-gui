/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

/**
 * Defines operation modes for JSON mapping in {@link JsonMapper}
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum TreeHandling {

    /** Defines static transformation <b>without</b> incorporation of value replacement */
    STATIC,

    /** Defines dynamic transformation with incorporation of value replacement */
    DYNAMIC;
}
