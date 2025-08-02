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
     * Extract {@link Enum}s to a {@link InputValueList} with the inclusion of the {@code selection} and a automatic translation.
     * <p>
     * If the enums can not be translated, the text will be 'I18N [var] not set.' with 'var' taken from {@link Enum#name()}.
     * 
     * @param <E> enum to use as value parameters
     * @param selection the selected value. can be null
     * @param enumClass enum to use as values
     * @param locale language information to use
     * @return {@code InputValueList} containing the selection list with either the selected value or the first list value checked
     */
    public <E extends Enum<E>> InputValueList mapSelectionValue(E selection, Class<E> enumClass, Locale locale)
    {
        return mapSelectionValue(
            selection.name(),
            Arrays
                .stream(enumClass.getEnumConstants())
                .map(e -> e.name())
                .toList(),
            locale);
    }

    /**
     * Extract {@link Enum}s to a {@link InputValueList} with the inclusion of the {@code selection}
     * 
     * @param selection the selected value. can be null
     * @param values list of values to use
     * @param locale language information to use
     * @return {@code InputValueList} containing the selection list with either the selected value or the first list value checked
     */
    public InputValueList mapSelectionValue(String selection, List<String> values, Locale locale)
    {
        InputValueList list = new InputValueList();

        values.forEach(value -> {
            String  text    = translationProvider.getText(locale, value);
            Boolean checked = selection != null && value.equals(selection);

            list.add(
                InputValue
                    .builder()
                    .text(text)
                    .value(value)
                    .checked(checked)
                    .build());
        });

        return list;
    }
}
