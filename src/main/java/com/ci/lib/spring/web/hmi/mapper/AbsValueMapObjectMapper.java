package com.ci.lib.spring.web.hmi.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.ci.lib.spring.web.hmi.input.util.InputValue;
import com.ci.lib.spring.web.hmi.input.util.InputValueList;
import com.ci.lib.spring.web.i18n.TranslationProvider;

public abstract class AbsValueMapObjectMapper<T>
{

    private final TranslationProvider translationProvider;

    protected AbsValueMapObjectMapper(TranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Export the values of an {@code obj} to a new {@link ValueMap}
     * 
     * @param obj object to export
     * 
     * @return a new {@code ValueMap} filled with the extracted fields from this
     */
    public final ValueMap export(final T obj)
    {
        return exportInternal(obj, new ValueMap(), new Locale("none"));
    }

    /**
     * Export the values of an {@code obj} to a {@link ValueMap}
     * 
     * @param obj object to export
     * @param valueMap to fill
     * @param locale local language to use for fields
     * @param translation to use
     * 
     * @return a {@code ValueMap} filled with the extracted fields from this
     */
    public final ValueMap export(final T obj, ValueMap valueMap, Locale locale)
    {
        return exportInternal(obj, valueMap, locale);
    }

    protected abstract ValueMap exportInternal(final T obj, ValueMap valueMap, Locale locale);

    /**
     * Extract {@link Enum}s to a {@link InputValueList} with the inclusion of the {@code selection} and
     * a automatic translation.<br>
     * If the enums cannot be translated, the text will be 'I18N [enum] not set.'
     * 
     * @param <E> enum to use as value parameters
     * @param selection the selected value. can be null
     * @param enumClass enum to use as values
     * @param locale language information to use
     * @param translations {@code TranslationMap} to fetch translated text from
     * 
     * @return {@code InputValueList} containing the selection list with either the selected value or
     *         the first list value checked
     */
    protected <E extends Enum<E>> InputValueList mapSelectionValue(E selection, Class<E> enumClass, Locale locale)
    {
        return mapSelectionValue(selection.name(), Arrays.stream(enumClass.getEnumConstants()).map(e -> e.name()).toList(), locale);
    }

    /**
     * Extract {@link Enum}s to a {@link InputValueList} with the inclusion of the {@code selection}
     * 
     * @param selection the selected value. can be null
     * @param values list of values to use *
     * @param locale language information to use
     * @param translations {@code TranslationMap} to fetch translated text from
     * 
     * @return {@code InputValueList} containing the selection list with either the selected value or
     *         the first list value checked
     */
    protected InputValueList mapSelectionValue(String selection, List<String> values, Locale locale)
    {
        final InputValueList list = new InputValueList();
        String               text;
        Boolean              checked;

        for (String value : values)
        {
            text = (String) translationProvider.getTranslationMap().getText(locale, value);
            checked = selection != null && value.equals(selection);

            list.add(InputValue.builder().text(text).value(value).checked(checked).build());
        }

        return list;
    }
}
