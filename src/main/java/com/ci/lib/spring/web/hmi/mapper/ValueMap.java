package com.ci.lib.spring.web.hmi.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ci.lib.spring.web.hmi.input.InputValue;
import com.ci.lib.spring.web.hmi.mapper.exception.ValueMapKeyAlreadyInUse;
import com.ci.lib.spring.web.hmi.mapper.exception.ValueMapSealedException;

import lombok.Data;

/**
 * Key/Value map to use for {@link TreeHandling#DYNAMIC}
 */
@Data
public class ValueMap
{

    private final Map<String, Object> map    = new HashMap<>();
    private Boolean                   sealed = false;

    /**
     * Add multiple {@link ValueMapExportable} objects to this map
     * 
     * @param objects
     */
    public void extractObjects(ValueMapExportable... objects)
    {
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
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, String value) throws ValueMapKeyAlreadyInUse
    {
        checkSeal();
        checkKey(key, String.class);
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
     * @throws IllegalArgumentException if {@code key} is already in use
     */
    public ValueMap add(String key, Boolean value) throws ValueMapKeyAlreadyInUse
    {
        checkSeal();
        checkKey(key, Boolean.class);
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
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, Integer value) throws ValueMapKeyAlreadyInUse
    {
        checkSeal();
        checkKey(key, Integer.class);
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
     * @throws ValueMapKeyAlreadyInUse if {@code key} is already in use
     */
    public ValueMap add(String key, List<InputValue> value) throws ValueMapKeyAlreadyInUse
    {
        checkSeal();
        checkKey(key, InputValue.class);
        map.put(key, value);

        return this;
    }

    /**
     * Retrieve a value associated to {@code key}
     * 
     * @param key to lookup
     * 
     * @return the value associated with {@code key}, or {@code null}
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
    private void checkKey(String key, Class<?> objectClass) throws ValueMapKeyAlreadyInUse
    {
        if (map.containsKey(key))
        {
            throw new ValueMapKeyAlreadyInUse(key, objectClass);
        }
    }

}
