/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

/**
 * Basic text blocks to use as fallbacks
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum BasicText {

    /** Default fallback for no text set */
    TEXT_NOT_SET("TNS"),

    /** Text for 'ok' button */
    OK("ok"),

    /** Text for 'cancel' button */
    CANCEL("cancel"),

    /** Text for 'help' button */
    HELP("help"),

    /** Text for 'close' button */
    CLOSE("close"),

    /** Text for 'close' button */
    NO_VALUE_SET("no value set"),

    /** Text for 'close' button */
    NO_TEXT_SET("no text set");

    private final String text;

    /**
     * Basic constructor
     * 
     * @param id of this text block
     * @param text to use as a fallback
     */
    BasicText(String text)
    {
        this.text = text;
    }

    /**
     * Get the fallback text assiciated with this enum
     * 
     * @return the fallback text
     */
    public final String getText()
    {
        return text;
    }
}
