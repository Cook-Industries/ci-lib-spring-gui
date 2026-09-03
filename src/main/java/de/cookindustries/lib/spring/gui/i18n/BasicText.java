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
public enum BasicText
{

    /** Default fallback for no text set */
    TEXT_NOT_SET("text-not-set", "TNS"),

    /** Text for 'ok' button */
    OK("ok", "ok"),

    /** Text for 'cancel' button */
    CANCEL("cancel", "cancel"),

    /** Text for 'help' button */
    HELP("help", "help"),

    /** Text for 'close' button */
    CLOSE("close", "close"),

    /** Text for 'close' button */
    NO_VALUE_SET("no.value-set", "no value set"),

    /** Text for 'close' button */
    NO_TEXT_SET("no-text-set", "no text set"),

    MANDATORY("mandatory", "mandatory"),

    INPUT_MANDATORY("input-mandatory", "Input {0} is mandatory.");

    private final String key;
    private final String text;

    /**
     * Basic constructor
     * 
     * @param id of this text block
     * @param text to use as a fallback
     */
    BasicText(String key, String text)
    {
        this.key = key;
        this.text = text;
    }

    /**
     * Get the fallback text assiciated with this enum
     * 
     * @return the fallback text
     */
    public final String getKey()
    {
        return key;
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
