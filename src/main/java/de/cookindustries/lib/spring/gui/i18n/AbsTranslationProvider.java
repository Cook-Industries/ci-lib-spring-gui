/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for creation of source based translations.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public abstract class AbsTranslationProvider
{

    private Map<Locale, Map<String, String>> translations = new ConcurrentHashMap<>();

    protected AbsTranslationProvider()
    {}

    /**
     * Method to be implemented by concrete class to fill the {@code Translations}.
     */
    protected abstract void initMaps();

    /**
     * Add a translation {@code text} based on a {@code locale} and {@code key}
     * 
     * @param locale to associate with
     * @param key to associate to
     * @param text to use
     * @throws IllegalArgumentException if {@code locale} is null, or if {@code key} or {@code text} are null or empty
     */
    protected final void addTranslation(Locale locale, String key, String text)
    {
        if (locale == null)
        {
            throw new IllegalArgumentException("translation locale cannot be null");
        }

        Map<String, String> map = translations.get(locale);

        if (map == null)
        {
            map = new ConcurrentHashMap<>();
            translations.put(locale, map);
        }

        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("translation key can not be null/empty");
        }

        if (text == null || text.isBlank())
        {
            throw new IllegalArgumentException("translation text can not be null/empty");
        }

        map.put(key, text);
    }

    /**
     * Get a translation from a {@link Locale} and a {@code key}
     * 
     * @param locale to associate
     * @param key to fetch
     * @return either the associated text, or "I18N [{@code key}] not set." if no associate exists
     * @throws IllegalArgumentException if {@code locale} is null, or if {@code key} is null or empty
     */
    public final String getText(Locale locale, String key)
    {
        if (locale == null)
        {
            throw new IllegalArgumentException("translation locale can not be null");
        }

        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("translation key can not be null/empty");
        }

        Map<String, String> map = translations.get(locale);

        return map != null && map.get(key) != null ? map.get(key) : String.format("I18N [%s] not set.", key);
    }
}
