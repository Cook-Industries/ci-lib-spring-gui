/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.ValueMapKeyAlreadyInUseException;
import de.cookindustries.lib.spring.gui.util.Sealable;
import de.cookindustries.lib.spring.gui.util.exception.ObjectSealedException;

/**
 * Key/Value map to use with {@link TreeHandling#DYNAMIC}.
 * <p>
 * This extends {@link Sealable} and will throw an {@link ObjectSealedException} if this object is sealed and a modification is tried.
 * <p>
 * Uses an underlying {@link ConcurrentHashMap} to ensure thread-safety.
 * 
 * @see Sealable
 */
public class ValueMap extends Sealable
{

    private final Map<String, Object> map = new ConcurrentHashMap<>();
    private final Integer             presedence;

    /**
     * Create a new {@code ValueMap} with presedence '0'
     */
    public ValueMap()
    {
        this(0);
    }

    /**
     * Create a new {@code ValueMap}
     * 
     * @param presedence in which order maps are resolved. Higher is better.
     */
    public ValueMap(Integer presedence)
    {
        super(ValueMap.class);

        this.presedence = presedence;
    }

    /**
     * Get the presedence for this map
     * 
     * @return the presedence of this map
     */
    public Integer getPresedence()
    {
        return presedence;
    }

    /**
     * Add a {@link String} value to this map
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUseException if {@code key} is already in use
     */
    public ValueMap add(String key, String value)
    {
        checkSeal();
        checkKey(key);

        map.put(key, value);

        return this;
    }

    /**
     * Add a {@link Boolean} value to this map
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws IllegalArgumentException if {@code key} is already in use
     */
    public ValueMap add(String key, Boolean value)
    {
        checkSeal();
        checkKey(key);

        map.put(key, value);

        return this;
    }

    /**
     * Add a {@link Integer} value to this map
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUseException if {@code key} is already in use
     */
    public ValueMap add(String key, Integer value)
    {
        checkSeal();
        checkKey(key);

        map.put(key, value);

        return this;
    }

    /**
     * Add a list of {@link InputValue}s value to this map
     * 
     * @param key to link
     * @param value to link
     * @return {@code this}
     * @throws ObjectSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUseException if {@code key} is already in use
     */
    public ValueMap add(String key, InputValueList value)
    {
        checkSeal();
        checkKey(key);

        map.put(key, value);

        return this;
    }

    /**
     * Retrieve a value associated to {@code key}
     * 
     * @param key to lookup
     * @return the value associated with {@code key}, or {@code null} if no key is set
     */
    public Object get(String key)
    {
        return map.get(key);
    }

    /**
     * Check whether {@code key} is already in use
     * 
     * @param key to check
     * @throws ValueMapKeyAlreadyInUseException if key is already used
     */
    private void checkKey(String key)
    {
        if (map.containsKey(key))
        {
            throw new ValueMapKeyAlreadyInUseException(key, map.get(key).getClass());
        }
    }

}
