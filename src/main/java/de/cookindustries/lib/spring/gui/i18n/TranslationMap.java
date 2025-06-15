package de.cookindustries.lib.spring.gui.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cookindustries.lib.spring.gui.util.Sealable;

/**
 * Aggrigation for translations
 */
public final class TranslationMap extends Sealable
{

    private Map<Locale, Map<String, String>> translations = new ConcurrentHashMap<>();

    public TranslationMap()
    {
        super(TranslationMap.class);
    }

    /**
     * Add a translation {@code text} based on a {@code locale} and {@code key}
     * 
     * @param locale to associate with
     * @param key to associate to
     * @param text to use
     * @throws IllegalArgumentException if {@code locale} is null
     * @throws IllegalArgumentException if {@code key} is null or empty
     * @throws IllegalArgumentException if {@code text} is null or empty
     */
    public void addTranslation(Locale locale, String key, String text)
    {
        checkSeal();

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
     * Add a whole map of translations. If {@code translationMap} is null, a empty map will be associated
     * 
     * @param locale to associate with
     * @param translationMap to add
     * @throws IllegalArgumentException if {@code locale} is null
     */
    public void addTranslations(Locale locale, Map<String, String> translationMap)
    {
        checkSeal();

        if (locale == null)
        {
            throw new IllegalArgumentException("translation local can not be null");
        }

        Map<String, String> map = translations.get(locale);

        if (map == null)
        {
            map = new ConcurrentHashMap<>();
            translations.put(locale, map);
        }

        if (translationMap != null)
        {
            map.putAll(translationMap);
        }
    }

    /**
     * Get a translation from a {@code locale} and a {@code key}
     * 
     * @param locale to fetch
     * @param key to fetch
     * @return either the associated text, or "{@code key} not set." if no associate exists
     * @throws IllegalArgumentException if {@code locale} is null, or if {@code key} is null or empty
     */
    public String getText(Locale locale, String key)
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
