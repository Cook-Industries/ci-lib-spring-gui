/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.KeyAlreadyInUseException;
import de.cookindustries.lib.spring.gui.util.Sealable;
import de.cookindustries.lib.spring.gui.util.exception.ObjectSealedException;

/**
 * Key/Value map to use for keyword replacement.
 * <p>
 * This extends {@link Sealable} and will throw an {@link ObjectSealedException} if this object is sealed and a modification is tried.
 * <p>
 * Uses an underlying {@link ConcurrentHashMap} to ensure thread-safety.
 * 
 * @see Sealable
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class KeyReplacmentMap extends Sealable
{

    private final Map<String, Object>          values;
    private final Map<String, String>          classes;
    private final Map<String, AbsFunctionCall> functions;

    private final int                          presedence;

    /**
     * Create a new {@code ValueMap} with presedence '0'
     */
    public KeyReplacmentMap()
    {
        this(0);
    }

    /**
     * Create a new {@code ValueMap}
     * 
     * @param presedence in which order maps are resolved. Higher is better.
     */
    public KeyReplacmentMap(Integer presedence)
    {
        super(KeyReplacmentMap.class);

        this.values = new ConcurrentHashMap<>();
        this.classes = new ConcurrentHashMap<>();
        this.functions = new ConcurrentHashMap<>();

        this.presedence = presedence;
    }

    /**
     * Check whether {@code key} is already in use
     * 
     * @param key to check
     * @throws KeyAlreadyInUseException if key is already used
     */
    private void checkValueKey(String key)
    {
        if (values.containsKey(key))
        {
            throw new KeyAlreadyInUseException(key, values.get(key).getClass(), "value");
        }
    }

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
     * Add a {@link String} value
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws KeyAlreadyInUseException if {@code key} is already in use
     */
    public KeyReplacmentMap value(String key, String value)
    {
        checkSeal();
        checkValueKey(key);

        values.put(key, value);

        return this;
    }

    /**
     * Add a {@link Boolean} value
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws IllegalArgumentException if {@code key} is already in use
     */
    public KeyReplacmentMap value(String key, Boolean value)
    {
        checkSeal();
        checkValueKey(key);

        values.put(key, value);

        return this;
    }

    /**
     * Add a {@link Integer} value
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws KeyAlreadyInUseException if {@code key} is already in use
     */
    public KeyReplacmentMap value(String key, Integer value)
    {
        checkSeal();
        checkValueKey(key);

        values.put(key, value);

        return this;
    }

    /**
     * Add a list of {@link InputValue}s value
     * 
     * @param key to link
     * @param list to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws KeyAlreadyInUseException if {@code key} is already in use
     */
    public KeyReplacmentMap value(String key, InputValueList list)
    {
        checkSeal();
        checkValueKey(key);

        values.put(key, list);

        return this;
    }

    /**
     * Add a {@link String} as class value
     * 
     * @param key to link
     * @param clazz to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws KeyAlreadyInUseException if {@code key} is already in use
     */
    public KeyReplacmentMap clazz(String key, String clazz)
    {
        checkSeal();

        if (classes.containsKey(key))
        {
            throw new KeyAlreadyInUseException(key, String.class, "class");
        }

        classes.put(key, clazz);

        return this;
    }

    /**
     * Add a {@link AbsFunctionCall} value
     * 
     * @param key to link
     * @param call to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws KeyAlreadyInUseException if {@code key} is already in use
     */
    public KeyReplacmentMap function(String key, AbsFunctionCall call)
    {
        checkSeal();

        if (functions.containsKey(key))
        {
            throw new KeyAlreadyInUseException(key, functions.get(key).getClass(), "function");
        }

        functions.put(key, call);

        return this;
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

}
