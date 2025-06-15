/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.i18n;

/**
 * Basic text blocks to use as fallbacks
 */
public enum BasicText {

    TEXT_NOT_SET(0, "TNS"), OK(1, "ok"), CANCEL(2, "cancel"), HELP(3, "help"), CLOSE(4, "close");

    private final Integer id;
    private final String  fallback;

    BasicText(Integer id, String fallback)
    {
        this.id = id;
        this.fallback = fallback;
    }

    public final Integer getID()
    {
        return id;
    }

    public final String getFallback()
    {
        return fallback;
    }
}
