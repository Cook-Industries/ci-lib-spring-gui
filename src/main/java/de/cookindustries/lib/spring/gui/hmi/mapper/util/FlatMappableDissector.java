/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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

    private static final Logger          LOG                           = LoggerFactory.getLogger(FlatMappableDissector.class);

    private static final int             UNHEALTHY_DEPTH               = 25;
    private static final String          RECURSION_UNHEALTHY_DEPTH_MSG =
        "recursion in FlatMappable object has reached an unhealthy depth >" + UNHEALTHY_DEPTH;

    private static final String          INDENT                        = "  ";

    private static final String          TRANSLATION_INDICATOR         = "$$text$";

    private static final List<String>    RAW_BLACKLIST                 = List.of(
        "^notifyAll$",
        "^notify$",
        "^clone$",
        "^hashCode$",
        "^toString$",
        "^equals$",
        "^wait$",
        "^class$",
        "classLoader",
        "^classes$",
        "^FunctionCalls$");

    private static final List<Pattern>   BLACKLIST_PATTERNS            =
        RAW_BLACKLIST
            .stream()
            .map(pat -> Pattern.compile(pat, Pattern.CASE_INSENSITIVE))
            .toList();

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

        List<String>        includedMethods = obj.includedMethods();
        Map<String, Object> flatMap         = new LinkedHashMap<>();

        flatten("", obj, includedMethods, flatMap, locale, 0);

        return TokenMap
            .builder()
            .presedence(2 * presedence + 10)
            .values(flatMap)
            .classes(obj.getClasses())
            .functions(obj.getFunctionCalls())
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
    private void flatten(String prefix, Object obj, List<String> includedMethods, Map<String, Object> result, Locale locale, int depth)
    {
        if (obj == null)
        {
            return;
        }

        String   indent  = INDENT.repeat(depth);

        Class<?> current = obj.getClass();

        LOG.trace("{}dissect object, type [{}]", indent, obj.getClass().getSimpleName());

        while (current != null && current != Object.class)
        {
            for (Method method : current.getMethods())
            {
                try
                {
                    String   rawName = method.getName();
                    Class<?> type    = method.getReturnType();
                    String   stripped;

                    if (rawName.startsWith("get"))
                    {
                        stripped = rawName.substring(3);
                    }
                    else if (rawName.startsWith("is"))
                    {
                        stripped = rawName.substring(2);
                    }
                    else if (includedMethods.contains(rawName))
                    {
                        stripped = rawName;
                    }
                    else
                    {
                        continue;
                    }

                    if (stripped.length() < 1)
                    {
                        continue;
                    }

                    if (BLACKLIST_PATTERNS
                        .stream()
                        .anyMatch(p -> p.matcher(stripped).matches()))
                    {
                        LOG.trace("{}ignore [{}] due to blacklisted", indent, stripped);

                        continue;
                    }

                    String fieldName = Character.toLowerCase(stripped.charAt(0)) + stripped.substring(1);

                    if (method.getParameterCount() > 0)
                    {
                        continue;
                    }

                    try
                    {
                        LOG.trace("{}dissect field [{}] type [{}]", indent, fieldName, type.getSimpleName());

                        String key   = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;

                        Object value = method.invoke(obj);

                        if (value == null)
                        {
                            LOG.trace("{} - field is null", indent);

                            continue;
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
                                        handleSimple(mapKey, entry.getValue(), result, locale, depth);
                                    }
                                    else
                                    {
                                        Object imp = entry.getValue();

                                        goDeeper(mapKey, imp, result, locale, depth + 1);
                                    }
                                }

                                continue;
                            }
                        }
                        else if (isSimple(value.getClass()))
                        {
                            handleSimple(key, value, result, locale, depth);
                        }
                        else if (value instanceof Collection<?> collection)
                        {
                            LOG.trace("{} - field is List", indent);

                            List<FlatMappable> items = new ArrayList<>();

                            for (Object element : collection)
                            {
                                if (element instanceof FlatMappable item)
                                {
                                    items.add(item);
                                }
                            }

                            if (!items.isEmpty())
                            {
                                result.put(key, new FlatMappableList<>(items));
                            }
                        }
                        else if (value.getClass().isArray())
                        {
                            LOG.trace("{} - field is Array", indent);

                            List<FlatMappable> items  = new ArrayList<>();

                            int                length = Array.getLength(value);

                            for (int i = 0; i < length; i++)
                            {
                                Object element = Array.get(value, i);

                                if (element instanceof FlatMappable item)
                                {
                                    items.add(item);
                                }
                            }

                            if (!items.isEmpty())
                            {
                                result.put(key, new FlatMappableList<>(items));
                            }
                        }
                        else
                        {
                            goDeeper(key, value, result, locale, depth + 1);
                        }
                    }
                    catch (Exception ex)
                    {
                        LOG.error("dissection went wrong", ex);
                    }
                }
                catch (Exception ex)
                {
                    LOG.error("method eval failed", ex);
                }
            }

            current = current.getSuperclass();
        }
    }

    /**
     * Go deeper into this nested object.
     * <p>
     * If the depth reaches a {@link #UNHEALTHY_DEPTH} the return value will be {@link #RECURSION_UNHEALTHY_DEPTH_MSG} and the digging will
     * stop. This is to prevent death-loops of endless recursion until a stackoverflow occurs.
     * 
     * @param key of the field/method
     * @param imp object to dig into
     * @param result map to store within
     * @param locale to fetch a translation if necessary
     * @param depth of the recursion
     */
    private void goDeeper(String key, Object imp, Map<String, Object> result, Locale locale, int depth)
    {
        String indent = INDENT.repeat(depth);

        if (depth > UNHEALTHY_DEPTH)
        {
            LOG.error(RECURSION_UNHEALTHY_DEPTH_MSG);
            handleSimple(key, RECURSION_UNHEALTHY_DEPTH_MSG, result, locale, depth);
        }

        LOG.trace("{} - field is nested go deeper", indent);

        List<String> inclMeth = List.of();

        if (imp instanceof FlatMappable flatImp)
        {
            inclMeth = flatImp.includedMethods();
        }

        flatten(key, imp, inclMeth, result, locale, depth + 1);
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
    private void handleSimple(String key, Object value, Map<String, Object> result, Locale locale, int depth)
    {
        String indent = INDENT.repeat(depth);

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
