/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;

/**
 * A {@code Service} to dissect {@link FlatMappable} objects to use them in a repetition instantiation of {@code Components}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Service
public final class FlatMappableDissector
{

    private static final String          TRANSLATION_INDICATOR = "$$";

    private static final Logger          LOG                   = LoggerFactory.getLogger(FlatMappableDissector.class);

    private final ObjectMapper           objectMapper;
    private final AbsTranslationProvider translationProvider;

    /**
     * Create a {@code FlatMappableDissector}.
     */
    public FlatMappableDissector(AbsTranslationProvider translationProvider)
    {
        this.objectMapper = new ObjectMapper();
        this.translationProvider = translationProvider;
    }

    /**
     * Dissect a {@link FlatMappable} object and map it to a {@link Map}.
     * 
     * @param obj to dissect
     * @param presedence to add to the resulting {@code TokenMap}
     * @param locale to use to translate
     * @return the filled unmodifiable {@code Map}, or {@code null} if the input was {@code null}
     */
    public TokenMap dissect(FlatMappable obj, int presedence, Locale locale)
    {
        if (obj == null)
        {
            return null;
        }

        Map<String, Object> nestedMap = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});

        nestedMap.remove("classes");
        nestedMap.remove("calls");

        Map<String, Object> flatMap = new LinkedHashMap<>();

        flatten("", nestedMap, flatMap, locale);

        return TokenMap
            .builder()
            .presedence(10 + 2 * presedence)
            .values(flatMap)
            .classes(obj.getClasses())
            .functions(obj.getCalls())
            .build();
    }

    /**
     * Flatten a given {@link Object}.
     * 
     * @param prefix to insert befor variable names in nested calls
     * @param value to flatten
     * @param result the result map to fill
     */
    private void flatten(String prefix, Object value, Map<String, Object> result, Locale locale)
    {
        if (value == null)
        {
            return;
        }

        if (value instanceof Map<?, ?> map)
        {
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                Object k = entry.getKey();

                if (!(k instanceof String))
                {
                    continue;
                }

                String key = prefix.isEmpty() ? (String) k : prefix + "." + k;

                flatten(key, entry.getValue(), result, locale);
            }
        }
        else if (value instanceof List<?> list)
        {
            flatten(prefix, list.stream().map(String::valueOf).collect(Collectors.joining(" ")), result, locale);
        }
        else if (value != null && value.getClass().isArray())
        {
            LOG.warn("arrays not supported");
        }
        else if (value instanceof Boolean bool)
        {
            result.put(prefix, bool);
        }
        else
        {
            String var = String.valueOf(value);

            if (var.startsWith(TRANSLATION_INDICATOR))
            {
                result.put(prefix, translationProvider.getText(locale, var.substring(TRANSLATION_INDICATOR.length())));
            }
            else
            {
                result.put(prefix, var);
            }

        }
    }
}
