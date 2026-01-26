/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

/**
 * Enum values for content interaction in {@link ContentResponse}
 * 
 * @since 3.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum ContentHandling {

    /** Append the content inside the target id */
    APPEND,

    /** Prepend the content inside the target id */
    PREPEND,

    /** Replace the complete id target */
    REPLACE,

    /** Delete the complete id target */
    DELETE;

}
