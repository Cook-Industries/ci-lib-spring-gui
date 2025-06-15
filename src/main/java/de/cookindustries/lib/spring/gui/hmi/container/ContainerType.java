/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

/**
 * This enum defines all the possible {@link Container} types
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
    BUTTON_BAR;

}
