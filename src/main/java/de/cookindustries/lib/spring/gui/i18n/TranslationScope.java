/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines where a translation may be used. Only {@link #CLIENT} translations are ever sent to a browser.
 *
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum TranslationScope
{

    /** Translation is used on the server only and never leaves it. */
    SERVER,

    /** Translation is used by the browser and delivered by the {@link UiTranslationResolver}. */
    CLIENT;

    /**
     * Parse a scope case insensitive.
     *
     * @param value to parse, {@code null} or blank results in {@link #SERVER}
     * @return the parsed scope
     * @throws IllegalArgumentException if {@code value} is no scope
     */
    @JsonCreator
    public static TranslationScope from(String value)
    {
        return value == null
            || value.isBlank()
                ? SERVER
                : valueOf(value.trim().toUpperCase());
    }
}
