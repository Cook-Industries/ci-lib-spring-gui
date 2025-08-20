/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;

@Component
public final class DynamicValueUtil
{

    private final AbsTranslationProvider translationProvider;

    protected DynamicValueUtil(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Extract {@link Enum}s to a {@link InputValueList} with automatic translation.
     * <p>
     * If the enums can not be translated, the text will be 'I18N [var] not set.' with 'var' taken from {@link Enum#name()}.
     * 
     * @param <E> enum to use as value parameters
     * @param enumClass enum to use as values
     * @param locale language information to use
     * @return {@code InputValueList} containing the selection list with either the selected value or the first list value checked
     */
    public <E extends Enum<E>> InputValueList mapSelectionValue(Class<E> enumClass, Locale locale)
    {
        InputValueList list          = new InputValueList();
        String         enumClassName = enumClass.getSimpleName();

        Arrays
            .stream(enumClass.getEnumConstants())
            .forEach(enu -> {
                String text = translationProvider.getText(locale, enumClassName + "." + enu.name());

                list.add(
                    InputValue
                        .builder()
                        .text(text)
                        .value(enu.name())
                        .build());
            });

        return list;
    }

    /**
     * Extract a list of {@link String}s to a {@link InputValueList}
     * 
     * @param values list of values to use
     * @param locale language information to use
     * @return {@code InputValueList} containing the selection list with either the selected value or the first list value checked
     */
    public InputValueList mapSelectionValue(List<String> values, Locale locale)
    {
        InputValueList list = new InputValueList();

        values.forEach(value -> {
            String text = translationProvider.getText(locale, value);

            list.add(
                InputValue
                    .builder()
                    .text(text)
                    .value(value)
                    .build());
        });

        return list;
    }
}
