/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Resolves the translations the browser may use: the {@link TranslationScope#CLIENT} translations of an {@link AbsTranslationProvider} and
 * the {@link BasicText} defaults with the same scope as the fallback for everything the provider does not define.
 * <p>
 * The endpoint that delivers them is written by the application, so security stays with it. The browser side of the library expects:
 * <ul>
 * <li>{@code GET <cook-industries.web.translation.url>/checksum} answering {@link UiTranslations#checksum()} as text,</li>
 * <li>{@code GET <cook-industries.web.translation.url>} answering {@link UiTranslations} as JSON,</li>
 * <li>{@code GET <cook-industries.web.translation.url>/fallback/checksum} answering {@link UiTranslations#checksum()} of
 * {@link #resolveFallbacks()} as text,</li>
 * <li>{@code GET <cook-industries.web.translation.url>/fallback} answering {@link #resolveFallbacks()} as JSON.</li>
 * </ul>
 * The first two for the {@link Locale} of the request, the fallbacks are independent of it.
 *
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class UiTranslationResolver
{

    /** Placeholder for the url of the translation endpoint, to be used in a mapping annotation */
    public static final String           TRANSLATION_BASE_URL_DEF = "${cook-industries.web.translation.url}";

    private final AbsTranslationProvider provider;

    /**
     * Construct a new instance
     * 
     * @param provider to take the translations from
     */
    public UiTranslationResolver(AbsTranslationProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Resolve the translations for the browser.
     * <p>
     * The provider is asked for the exact {@code requested} locale, then for any locale of the same language, then for English. Keys the
     * provider does not define are missing, the browser takes them from {@link #resolveFallbacks()}.
     *
     * @param requested locale of the request, can be {@code null}
     * @return the translations with their checksum
     */
    public UiTranslations resolve(Locale requested)
    {
        Map<String, String> texts = new TreeMap<>();
        Locale              match = match(requested);

        if (match != null)
        {
            texts.putAll(provider.getClientTexts(match));
        }

        return pack(texts);
    }

    /**
     * Resolve only the checksum for the browser.
     * 
     * @param requested locale of the request, can be {@code null}
     * @return the checksum of {@link #resolve(Locale)}
     */
    public String getChecksum(Locale requested)
    {
        return resolve(requested).checksum();
    }

    /**
     * Resolve the fallback texts for the browser. They are the single source of truth for the text to show if no translation is defined.
     *
     * @return the {@link TranslationScope#CLIENT} texts of {@link BasicText} with their checksum
     */
    public UiTranslations resolveFallbacks()
    {
        Map<String, String> fallbacks = new TreeMap<>();

        for (BasicText text : BasicText.values())
        {
            if (text.getScope() == TranslationScope.CLIENT)
            {
                fallbacks.put(text.getKey(), text.getText());
            }
        }

        return pack(fallbacks);
    }

    /**
     * Resolve only the checksum of the fallbacks for the browser.
     *
     * @return the checksum of {@link #resolveFallbacks()}
     */
    public String getFallbackChecksum()
    {
        return resolveFallbacks().checksum();
    }

    private UiTranslations pack(Map<String, String> sortedTexts)
    {
        return new UiTranslations(checksum(sortedTexts), Collections.unmodifiableMap(sortedTexts));
    }

    private Locale match(Locale requested)
    {
        Set<Locale> available = provider.getLocales();

        if (requested != null)
        {
            if (available.contains(requested))
            {
                return requested;
            }

            Locale sameLanguage = firstOfLanguage(available, requested.getLanguage());

            if (sameLanguage != null)
            {
                return sameLanguage;
            }
        }

        return firstOfLanguage(available, Locale.ENGLISH.getLanguage());
    }

    private Locale firstOfLanguage(Set<Locale> locales, String language)
    {
        return locales
            .stream()
            .filter(locale -> locale.getLanguage().equals(language))
            .min(Comparator.comparing(Locale::toString))
            .orElse(null);
    }

    private String checksum(Map<String, String> sortedTexts)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            sortedTexts.forEach((key, text) -> {
                digest.update(key.getBytes(StandardCharsets.UTF_8));
                digest.update((byte) 0);
                digest.update(text.getBytes(StandardCharsets.UTF_8));
                digest.update((byte) 0);
            });

            return HexFormat.of().formatHex(digest.digest());
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
