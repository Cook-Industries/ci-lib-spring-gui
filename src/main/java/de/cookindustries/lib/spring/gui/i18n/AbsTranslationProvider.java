/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for creation of {@link Locale} based translations.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public abstract class AbsTranslationProvider
{

    private static final Logger                    LOG         = LoggerFactory.getLogger(AbsTranslationProvider.class);

    private final Pattern                          PLACEHOLDER = Pattern.compile("\\{(\\d+)}");

    private final Map<Locale, Map<String, String>> translations;

    private final Map<Locale, Set<String>>         clientKeys;

    protected AbsTranslationProvider()
    {
        translations = new ConcurrentHashMap<>();
        clientKeys = new ConcurrentHashMap<>();
    }

    /**
     * Method to be implemented by concrete class to fill the {@code Translations}.
     */
    public abstract AbsTranslationProvider initMaps();

    /**
     * Add a {@link TranslationScope#SERVER} translation {@code text} based on a {@code locale} and {@code key}
     * 
     * @param locale to associate with
     * @param key to associate to
     * @param text to use
     * @throws IllegalArgumentException if {@code locale} is null, or if {@code key} or {@code text} are null or empty
     */
    protected final void addTranslation(Locale locale, String key, String text)
    {
        addTranslation(locale, key, text, TranslationScope.SERVER);
    }

    /**
     * Add a translation {@code text} based on a {@code locale} and {@code key}
     * 
     * @param locale to associate with
     * @param key to associate to
     * @param text to use
     * @param scope where the translation may be used. Only {@link TranslationScope#CLIENT} translations are sent to a browser
     * @throws IllegalArgumentException if {@code locale} or {@code scope} are null, or if {@code key} or {@code text} are null or empty
     */
    protected final void addTranslation(Locale locale, String key, String text, TranslationScope scope)
    {
        if (scope == null)
        {
            throw new IllegalArgumentException("translation scope cannot be null");
        }

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

        Set<String> keys = clientKeys.computeIfAbsent(locale, l -> ConcurrentHashMap.newKeySet());

        if (scope == TranslationScope.CLIENT)
        {
            keys.add(key);
        }
        else
        {
            keys.remove(key);
        }
    }

    /**
     * Get a translation from a {@link Locale} and a {@code key}
     * 
     * @param locale to associate
     * @param key to fetch
     * @param values placeholder fill-in values
     * @return either the associated text, or "I18N [{@code key}] not set." if no associate exists
     * @throws IllegalArgumentException if {@code locale} is null, or if {@code key} is null or empty
     */
    public final String getText(Locale locale, String key, Object... values)
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
        String              translation;

        if (map != null && map.containsKey(key))
        {
            String template = map.get(key);

            translation = format(key, locale, template, values);
        }
        else
        {
            translation = String.format("I18N [%s] not set.", key);

            LOG.warn("no translation defined for [{}]-[{}]", key, locale);
        }

        return translation;
    }

    /**
     * Get all {@link TranslationScope#CLIENT} translations of exactly this {@code locale}, sorted by key.
     * <p>
     * The texts are returned as templates, placeholders are not filled.
     * 
     * @param locale to fetch for
     * @return an unmodifiable map, empty if nothing is defined for {@code locale}
     * @throws IllegalArgumentException if {@code locale} is null
     */
    public final Map<String, String> getClientTexts(Locale locale)
    {
        if (locale == null)
        {
            throw new IllegalArgumentException("translation locale can not be null");
        }

        Map<String, String> texts = translations.get(locale);
        Set<String>         keys  = clientKeys.get(locale);
        Map<String, String> found = new TreeMap<>();

        if (texts != null && keys != null)
        {
            keys.forEach(key -> {
                String text = texts.get(key);

                if (text != null)
                {
                    found.put(key, text);
                }
            });
        }

        return Collections.unmodifiableMap(found);
    }

    /**
     * Get all {@link Locale}s that have at least one translation.
     * 
     * @return a snapshot of the known locales
     */
    public final Set<Locale> getLocales()
    {
        return Set.copyOf(translations.keySet());
    }

    /**
     * Format a translated {@code String} by filling out placeholders if present.
     * 
     * @param key of translation for logging
     * @param template the translated String
     * @param args arguments to fill in the placeholders
     * @return
     */
    private final String format(String key, Locale locale, String template, Object... args)
    {
        Matcher matcher  = PLACEHOLDER.matcher(template);

        int     maxIndex = 0;

        while (matcher.find())
        {
            int index = Integer.parseInt(matcher.group(1));

            if (index >= args.length)
            {
                LOG.warn("no translation placeholder value defined for [{}]:[{}] in [{}]", key, index, locale);
            }

            maxIndex = maxIndex < index ? index : maxIndex;
        }

        if (args.length > maxIndex)
        {
            LOG.warn("too many translation placeholder values submitted for [{}] in [{}] expected [{}] got [{}]", key, locale, maxIndex,
                args.length);
        }

        return new MessageFormat(template, args);
    }
}
