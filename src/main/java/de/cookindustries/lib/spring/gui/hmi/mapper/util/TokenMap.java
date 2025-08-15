/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
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
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * Key/Value map to use by the {@link JsonMapper} to replace {@code token}s.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
public final class TokenMap
{

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

}
