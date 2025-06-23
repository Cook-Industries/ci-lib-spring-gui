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

    /** Type for Tab {@link Container} */
    TAB,

    /** Type for Form {@link Container} */
    FORM,

    /** Type for general Content {@link Container} */
    CONTENT,

    /** Type for splitted {@link Container} */
    SPLITTED,

    /** Type for Text {@link Container} */
    TEXT,

    /** Type for Link {@link Container} */
    LINK,

    /** Type for hidden {@link Container} */
    HIDDEN,

    /** Type for Image {@link Container} */
    IMAGE,

    /** Type for Audio {@link Container} */
    AUDIO,

    /** Type for textual Button {@link Container} */
    BUTTON,

    /** Type for iconized Button {@link Container} */
    BUTTON_ICON,

    /** Type for ButtonBar {@link Container} */
    BUTTON_BAR,

    /** Type for Modal {@link Container} */
    MODAL;

}
