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

/**
 * This interface describes a {@code POJO} that can be mapped to key-value pairs in a {@link Map}.
 * <p>
 * Objects of this type should only have flat variable types, no {@code List} or {@code Array} structures. This limitation is based in the
 * fact that a transfomation by {@link FlatMappableDissector} cannot resolve {@code List} or {@code Array} objects to a viable flattened
 * structure which would be usable in a nested-loop.
 * <p>
 * {@code Map}s and nested {@code Object}s are allowed. Their variables will be constructed with a prefix of the variable name in this class
 * appended with the name of the variable in the nested object.
 * 
 * <pre>
 * Example:
 * 1st level field: PlayerCount pc;
 * nested fields in PlayerCount: text | number 
 * result entries in map: 
 *     pc.text   -&gt; Player 1
 *     pc.number -&gt; 3
 * </pre>
 * <p>
 * All predefined variables of this abstract class will be exported and removed before the object gets flattened. These exports are give to
 * the {@link JsonMapper} alongside the flattened {@link TokenMap}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public interface FlatMappable
{

    public default Map<String, String> getClasses()
    {
        return Map.of();
    }

    public default Map<String, AbsFunctionCall> getFunctionCalls()
    {
        return Map.of();
    }

    public default List<String> includedMethods()
    {
        return List.of();
    }

}
