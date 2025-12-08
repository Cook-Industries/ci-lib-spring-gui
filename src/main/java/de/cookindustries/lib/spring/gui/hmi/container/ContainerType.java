/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

/**
 * This enum defines all the possible {@link Container} types
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum ContainerType {

    /** Type for tab {@link Container} */
    TAB,

    /** Type for form {@link Container} */
    FORM,

    /** Type for general content {@link Container} */
    CONTENT,

    /** Type for splitted {@link Container} */
    SPLITTED,

    /** Type for text {@link Container} */
    TEXT,

    /** Type for text headers {@link Container} */
    HEADING,

    /** Type for link {@link Container} */
    LINK,

    /** Type for hidden {@link Container} */
    HIDDEN,

    /** Type for image {@link Container} */
    IMAGE,

    /** Type for audio {@link Container} */
    AUDIO,

    /** Type for textual button {@link Container} */
    BUTTON,

    /** Type for iconized button {@link Container} */
    BUTTON_ICON,

    /** Type for button bar {@link Container} */
    BUTTON_BAR,

    /** Type for burger menu {@link Container} */
    BURGER,

    /** Type for modal {@link Container} */
    MODAL,

    /** Type for table {@link Container} */
    TABLE,

    /** Type for table row {@link Container} */
    TABLE_ROW,

    /** Type for a empty (no) container */
    EMPTY,

    /** Type for a svg container */
    SVG;

}
