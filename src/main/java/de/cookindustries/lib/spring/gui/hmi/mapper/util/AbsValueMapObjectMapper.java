/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public abstract class AbsValueMapObjectMapper<T>
{

    private final AbsTranslationProvider translationProvider;

    protected AbsValueMapObjectMapper(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Export the values of an {@code obj} to a new {@link KeyReplacmentMap} in {@link Locale#ENGLISH}
     * 
     * @param obj object to export
     * @return a new {@code ValueMap} filled with the extracted fields from this
     */
    public final KeyReplacmentMap export(final T obj)
    {
        return exportInternal(obj, new KeyReplacmentMap(), Locale.ENGLISH);
    }

    /**
     * Export the values of an {@code obj} to a {@link KeyReplacmentMap}
     * 
     * @param obj object to export
     * @param valueMap to fill
     * @param locale local language to use for fields
     * @return a {@code ValueMap} filled with the extracted fields from this
     */
    public final KeyReplacmentMap export(final T obj, KeyReplacmentMap valueMap, Locale locale)
    {
        return exportInternal(obj, valueMap, locale);
    }

    protected abstract KeyReplacmentMap exportInternal(final T obj, KeyReplacmentMap valueMap, Locale locale);

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
    protected <E extends Enum<E>> InputValueList mapSelectionValue(E selection, Class<E> enumClass, Locale locale)
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
    protected InputValueList mapSelectionValue(String selection, List<String> values, Locale locale)
    {
        final InputValueList list = new InputValueList();
        String               text;
        Boolean              checked;

        for (String value : values)
        {
            text = translationProvider.getText(locale, value);
            checked = selection != null && value.equals(selection);

            list.add(
                InputValue
                    .builder()
                    .text(text)
                    .value(value)
                    .checked(checked)
                    .build());
        }

        return list;
    }
}
