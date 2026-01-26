/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.List;
import java.util.Map;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonMapper;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode.CacheStrategy;

/**
 * Key/Value map to use by the {@link JsonMapper} to replace {@code token}s.
 * <p>
 * This map expects all elements to be immutable.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@EqualsAndHashCode(cacheStrategy = CacheStrategy.LAZY)
public final class TokenMap
{

    private static final String                PREFIX     = "> %-";
    private static final String                FILL       = "s : %-";
    private static final String                SUFFIX     = "s : %s";

    @NonNull
    @Default
    private final Integer                      presedence = 0;

    @Singular
    private final Map<String, Object>          values;

    @Singular("clazz")
    private final Map<String, String>          classes;

    @Singular
    private final Map<String, AbsFunctionCall> functions;

    @Singular
    private final List<String>                 deactivateUids;

    @Singular("dissect")
    private final List<FlatMappable>           dissect;

    /**
     * Get the presedence for this map
     * 
     * @return the presedence of this map
     */
    public int getPresedence()
    {
        return presedence;
    }

    /**
     * Retrieve a value associated to {@code key}
     * 
     * @param key to lookup
     * @return the {@code value} associated with {@code key}, or {@code null} if no key is set
     */
    public Object getValue(String key)
    {
        return values.get(key);
    }

    /**
     * Retrieve a class associated to {@code key}
     * 
     * @param key to lookup
     * @return the {@code class} associated with {@code key}, or {@code null} if no key is set
     */
    public String getClazz(String key)
    {
        return classes.get(key);
    }

    /**
     * Retrieve a class associated to {@code key}
     * 
     * @param key to lookup
     * @return the {@code function} associated with {@code key}, or {@code null} if no key is set
     */
    public AbsFunctionCall getFunction(String key)
    {
        return functions.get(key);
    }

    /**
     * Checks whether a {@code uid} is active or not.
     * <p>
     * If the {@code uid} is not added via {@code #deactivateId()} this will return {@code true}.
     * 
     * @param uid to look up
     * @return true if {@code id} is not explicitly disabled in this map
     */
    public boolean isUidActive(String uid)
    {
        return !deactivateUids.contains(uid);
    }

    /**
     * Get the objects to dissect.
     * 
     * @return objects to dissect
     */
    public List<FlatMappable> getObjectsToDissect()
    {
        return dissect;
    }

    @Override
    public String toString()
    {
        StringConcat sc                    = new StringConcat();

        int          valuesStringLength    =
            values
                .keySet()
                .stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        String       formatStringValues    = PREFIX + valuesStringLength + SUFFIX;

        int          classesStringLength   =
            classes
                .keySet()
                .stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        String       formatStringClasses   = PREFIX + classesStringLength + SUFFIX;

        int          functionsStringLength =
            functions
                .keySet()
                .stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        int          paramsStringLength    =
            functions
                .values()
                .stream()
                .mapToInt(e -> getClass().getSimpleName().length())
                .max()
                .orElse(0);

        String       formatStringFunctions = PREFIX + functionsStringLength + FILL + paramsStringLength + SUFFIX;

        sc
            .appendnl("TokenMap")
            .append("hash: ")
            .appendnl(hashCode())
            .append("presedence: ")
            .appendnl(presedence)
            .append("# of elements to dissect: ")
            .appendnl(dissect.size())

            .appendnl(!values.isEmpty(), "values:")
            .appendnl(!values.isEmpty(), values.entrySet(),
                entry -> String.format(formatStringValues, entry.getKey(), String.valueOf(entry.getValue())))

            .appendnl(!classes.isEmpty(), "classes:")
            .appendnl(!classes.isEmpty(), classes.entrySet(),
                entry -> String.format(formatStringClasses, entry.getKey(), String.valueOf(entry.getValue())))

            .appendnl(!functions.isEmpty(), "functions:")
            .appendnl(!functions.isEmpty(), functions.entrySet(),
                entry -> String.format(formatStringFunctions, entry.getKey(),
                    entry.getValue().getClass().getSimpleName(), entry.getValue().parseAsJS()))

            .appendnl(!deactivateUids.isEmpty(), "deactivated uids:")
            .appendnl(!deactivateUids.isEmpty(), deactivateUids, uid -> "> " + uid);

        return sc.toString();
    }

}
