/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.annotation.Configuration;

/**
 * Properties for the {@link FlatMappableDissector} service.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Configuration
public class FlatMappableDissectorProperties
{

    private final Map<Class<?>, Function<Object, String>> transformers;

    /**
     * Create the properties with default {@code transformers} for Java base classes.
     */
    public FlatMappableDissectorProperties()
    {
        transformers = new HashMap<>();

        transformers.put(String.class, s -> s.toString());
        transformers.put(Integer.class, i -> String.valueOf(i));
        transformers.put(Double.class, d -> String.valueOf(d));
        transformers.put(Long.class, l -> String.valueOf(l));
        transformers.put(Character.class, c -> String.valueOf(c));
        transformers.put(Date.class, d -> String.valueOf(d));
    }

    /**
     * Register an additional {@code transformer} for a custom-to-string transformation.
     * 
     * @param clazz to map from
     * @param transform to use
     */
    public void registerTransformer(Class<?> clazz, Function<Object, String> transform)
    {
        if (!transformers.containsKey(clazz))
        {
            transformers.put(clazz, transform);
        }
    }

    /**
     * Get all registered {@code transformer}.
     * 
     * @return a {@code Map} containing all registerd transformers
     */
    public final Map<Class<?>, Function<Object, String>> getTransformers()
    {
        return Collections.unmodifiableMap(transformers);
    }
}
