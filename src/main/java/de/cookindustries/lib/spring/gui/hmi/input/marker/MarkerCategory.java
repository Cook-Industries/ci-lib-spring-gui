/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.marker;

/**
 * Describes the category to which a marker is bound.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum MarkerCategory {

    /** Anything related to warnings */
    WARNING,

    /** Anything related to errors */
    ERROR,

    /** Anything to highlight via Modal */
    MODAL;

}
