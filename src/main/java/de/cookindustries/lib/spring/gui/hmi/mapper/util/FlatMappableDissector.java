/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import jakarta.annotation.PostConstruct;

/**
 * A {@code Service} to dissect {@link FlatMappable} objects to use them in a repetition instantiation of {@code Components}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Service
public final class FlatMappableDissector
{

    private static final Logger          LOG                   = LoggerFactory.getLogger(FlatMappableDissector.class);

    private static final String          TRANSLATION_INDICATOR = "$$text$";

    private static final List<String>    BLACKLIST_PARAMNAMES  = List.of("classes", "calls");

    private static FlatMappableDissector holder;

    private final AbsTranslationProvider translationProvider;

    /**
     * Create a {@code FlatMappableDissector}.
     */
    public FlatMappableDissector(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    @PostConstruct
    public void init()
    {
        holder = this;
    }

    static FlatMappableDissector instance()
    {
        return holder;
    }

    /**
     * Dissect a {@link FlatMappable} object and map it to a {@link Map}.
     * 
     * @param obj to dissect
     * @param presedence to add to the resulting {@code TokenMap}
     * @param locale to use to translate
     * @return the filled unmodifiable {@code Map}, or {@code null} if the input was {@code null}
     */
    public <T extends FlatMappable> TokenMap dissect(T obj, int presedence, Locale locale)
    {
        if (obj == null)
        {
            return null;
        }

        Map<String, Object> flatMap = new LinkedHashMap<>();

        flatten("", obj, flatMap, locale, 0);

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
     * @param locale to lookup translations with
     * @param depth of the recursive call
     */
    private void flatten(String prefix, Object obj, Map<String, Object> result, Locale locale, int depth)
    {
        if (obj == null)
        {
            return;
        }

        String   indent  = "  ".repeat(depth);

        Class<?> current = obj.getClass();

        LOG.trace("{}dissect object, type [{}]", indent, obj.getClass().getSimpleName());

        while (current != null && current != Object.class)
        {
            for (Field field : current.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()))
                {
                    continue;
                }

                try
                {
                    String key = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

                    LOG.trace("{}dissect field [{}] type [{}]", indent, key, field.getType().getSimpleName());

                    if (BLACKLIST_PARAMNAMES.contains(key))
                    {
                        LOG.trace("{} - ignore it since on blacklist", indent);

                        return;
                    }

                    String name              = field.getName();
                    String capitalized       = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    String getterName        = "get" + capitalized;
                    String booleanGetterName = "is" + capitalized;

                    Method getter            = null;

                    try
                    {
                        if (field.getType() == boolean.class || field.getType() == Boolean.class)
                        {
                            getter = findMethod(current, booleanGetterName);
                        }

                        if (getter == null)
                        {
                            getter = findMethod(current, getterName);
                        }
                    }
                    catch (NoSuchMethodException ex)
                    {
                        LOG.warn("failed to find get/is method for {}. ({})", name, ex.getMessage());

                        continue;
                    }

                    Object value = null;

                    if (getter != null && getter.canAccess(obj))
                    {
                        try
                        {
                            value = getter.invoke(obj);
                        }
                        catch (Exception ex)
                        {
                            LOG.trace("{} - getter for field [{}] is not accessable", indent, key);

                            continue;
                        }
                    }
                    else
                    {
                        LOG.trace("{} - no getter for field [{}] found", indent, key);

                        continue;
                    }

                    if (value == null)
                    {
                        LOG.trace("{} - field is null", indent);

                        return;
                    }
                    else if (value instanceof FlatMappable || value instanceof FlatMappableList)
                    {
                        LOG.trace("{} - field is {}/{}, add unmodified", " ".repeat(depth + 1), FlatMappable.class.getSimpleName(),
                            FlatMappableList.class.getSimpleName());

                        result.put(key, value);
                    }
                    else if (value instanceof Map<?, ?> map)
                    {
                        LOG.trace("{} - field is {}, go deeper", " ".repeat(depth + 1), Map.class.getSimpleName());

                        for (Map.Entry<?, ?> entry : map.entrySet())
                        {
                            Object k = entry.getKey();

                            if ((k instanceof String ks))
                            {
                                String mapKey = key + "." + ks;

                                if (isSimple(entry.getValue().getClass()))
                                {
                                    handleSimple(mapKey, entry.getValue(), result, locale, indent);
                                }
                                else
                                {
                                    flatten(mapKey, entry.getValue(), result, locale, depth + 1);
                                }
                            }

                            continue;
                        }
                    }
                    else if (isSimple(value.getClass()))
                    {
                        handleSimple(key, value, result, locale, indent);
                    }
                    else if (value.getClass().isArray() || value instanceof Collection<?>)
                    {
                        LOG.trace("{} - field is List/Array, ignore it", indent);
                    }
                    else
                    {
                        LOG.trace("{} - field is nested go deeper", indent);

                        flatten(key, value, result, locale, depth + 1);
                    }
                }
                catch (Exception ex)
                {
                    LOG.error("dissection went wrong", ex);
                }
            }

            current = current.getSuperclass();
        }
    }

    /**
     * Write a base Java object to the result map.
     * 
     * @param key to associate
     * @param value to put in the map
     * @param result map to store within
     * @param locale to fetch a translation if necessary
     * @param indent for logging
     */
    private void handleSimple(String key, Object value, Map<String, Object> result, Locale locale, String indent)
    {
        LOG.trace("{} - field is simple type value [{}]", indent, String.valueOf(value));

        if (value instanceof String text)
        {
            if (text.startsWith(TRANSLATION_INDICATOR))
            {
                result.put(key, translationProvider.getText(locale, text.substring(TRANSLATION_INDICATOR.length())));

                return;
            }
        }

        result.put(key, value);
    }

    /**
     * Find a method to a {@code name} on an {@code Class}.
     * 
     * @param cls to check
     * @param name name to lookup
     * @return the found method
     * @throws NoSuchMethodException if no method exists
     */
    private static Method findMethod(Class<?> cls, String name) throws NoSuchMethodException
    {
        return cls.getMethod(name);
    }

    /**
     * Check if a class is a basic Java type.
     * 
     * @param type to check
     * @return {@code true}, if {@code type} is {@code primitve}, {@code String}, {@code Number}, {@code Boolean}, {@code CHaracter} or
     *         {@code Enum}, {@code false} otherwise
     */
    private static boolean isSimple(Class<?> type)
    {
        return type.isPrimitive()
            || type.equals(String.class)
            || Number.class.isAssignableFrom(type)
            || Boolean.class.isAssignableFrom(type)
            || Character.class.isAssignableFrom(type)
            || Enum.class.isAssignableFrom(type);
    }
}
