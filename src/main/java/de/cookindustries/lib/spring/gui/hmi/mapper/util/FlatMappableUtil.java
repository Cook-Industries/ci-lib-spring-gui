/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.hmi.mapper.util.elements.TranslatedTextElement;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;

/**
 * A utility class to create basic component-filler elements.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public final class FlatMappableUtil
{

    private final AbsTranslationProvider translationProvider;

    public FlatMappableUtil(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Create a list of {@link TranslatedTextElement}s to use for looped {@code GUI} creation.
     * 
     * @param locale to fetch texts for
     * @param keys to lookup
     * @return a list of {@code TranslatedTextElement}s in order of {@code keys} provided
     */
    public FlatMappableList createTranslatedTextElements(Locale locale, List<String> keys)
    {
        List<FlatMappable> list = new ArrayList<>();

        Collections.unmodifiableList(keys).forEach(key -> {
            list.add(new TranslatedTextElement(translationProvider.getText(locale, key)));
        });

        return FlatMappableList
            .builder()
            .elements(list)
            .build();
    }
}
