/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ci.lib.spring.web.hmi.input.InputValue;
import com.ci.lib.spring.web.hmi.input.InputValueList;
import com.ci.lib.spring.web.hmi.mapper.exception.ValueMapKeyAlreadyInUse;
import com.ci.lib.spring.web.hmi.mapper.exception.ValueMapSealedException;

import lombok.Data;

/**
 * Key/Value map to use for {@link TreeHandling#DYNAMIC}.<br>
 * Uses an underlying {@link ConcurrentHashMap} to ensure thread-safety.
 */
@Data
public class ValueMap
{

    private final Map<String, Object> map    = new ConcurrentHashMap<>();
    private final Integer             presedence;

    private Boolean                   sealed = false;

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
     * @param presedence in which concurrent maps are resolved. Higher is better.
     */
    public ValueMap(Integer presedence)
    {
        this.presedence = presedence;
    }

    /**
     * Add multiple {@link ValueMapExportable} objects to this map
     * 
     * @param objects
     * 
     * @throws ValueMapSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUse if a implicit {@code key} is already in use
     */
    public void extractObjects(ValueMapExportable... objects)
    {
        checkSeal();

        for (ValueMapExportable vme : objects)
        {
            vme.exportTo(this);
        }
    }

    /**
     * Seal this map so it cannot be changed anymore
     */
    public void seal()
    {
        this.sealed = true;
    }

    /**
     * Add a {@link String} value to this map
     * 
     * @param key to link
     * @param value to link
     * 
     * @return {@code this}
     * 
     * @throws ValueMapSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, String value) throws ValueMapKeyAlreadyInUse
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
     * 
     * @return {@code this}
     * 
     * @throws ValueMapSealedException if this map is already sealed
     * @throws IllegalArgumentException if {@code key} is already in use
     */
    public ValueMap add(String key, Boolean value) throws ValueMapKeyAlreadyInUse
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
     * 
     * @return {@code this}
     * 
     * @throws ValueMapSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, Integer value) throws ValueMapKeyAlreadyInUse
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
     * 
     * @return {@code this}
     * 
     * @throws ValueMapSealedException if this map is already sealed
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, InputValueList value) throws ValueMapKeyAlreadyInUse
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
     * 
     * @return the value associated with {@code key}, or {@code null} if no key is set
     */
    public Object get(String key)
    {
        return map.get(key);
    }

    /**
     * Check whether this map is sealed
     * 
     * @throws ValueMapSealedException if this map is sealed
     */
    private void checkSeal() throws ValueMapSealedException
    {
        if (sealed.booleanValue())
        {
            throw new ValueMapSealedException();
        }
    }

    /**
     * Check whether {@code key} is already in use
     * 
     * @param key to check
     * 
     * @throws ValueMapKeyAlreadyInUse if key is already used
     */
    private void checkKey(String key) throws ValueMapKeyAlreadyInUse
    {
        if (map.containsKey(key))
        {
            throw new ValueMapKeyAlreadyInUse(key, map.get(key).getClass());
        }
    }

}
