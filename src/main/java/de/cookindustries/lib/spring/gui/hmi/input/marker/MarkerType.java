/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.marker;

/**
 * Defines types of states in which a value can be
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum MarkerType {

    /** The value is expected but does not exist */
    NO_VALUE,

    /** The value is empty */
    EMPTY,

    /** The value is invalid */
    INVALID,

    /** The value is out of range */
    OUT_OF_RANGE,

    /** The value is to long */
    TOO_LONG,

    /** The value is not long enough */
    TOO_SHORT,

    /** The value is incomplete */
    INCOMPLETE,

    /** The value could not be parsed */
    NOT_PARSABLE,

    /** Non discrete value for free usage */
    CUSTOM;
}
