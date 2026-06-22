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

/**
 * This interface describes a {@code POJO} that can be mapped in a {@link TokenMap}.
 * <p>
 * {@code Map}s {@code Arrays} and nested {@code Object}s are allowed. But should be avoided where possible due to resolving them recursivly
 * can have unpredictable results. Their variables will be dissected with a prefix of the variable name in the pojo appended with the name
 * of the variable in the nested object. See example below.
 * <p>
 * Any method of the Pojo starting with:
 * <ul>
 * <li>is...</li>
 * <li>get...</li>
 * </ul>
 * will be automatically dissected.
 * <p>
 * {@code String} fields of this pojo can be automatically translated via the translate indicator {@code $$text$*name*}.
 * <p>
 * Example nested object:
 * 
 * <pre>
 * 1st level field: PlayerCount pc;
 * nested fields in PlayerCount: text | number
 * 
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

    /**
     * Get a list of styling classes for this object.
     * <p>
     * Default is empty.
     * 
     * @return map of styling classes
     */
    default Map<String, String> fmClasses()
    {
        return Map.of();
    }

    /**
     * Get a {@code Map} of {@link AbsFunctionCall}s for this object.
     * <p>
     * Default is empty.
     * 
     * @return map of function calls
     */
    default Map<String, AbsFunctionCall> fmFunctionCalls()
    {
        return Map.of();
    }

    /**
     * Get a list of names for methods of this pojo to {@code include} in the dissection process.
     * <p>
     * Default is empty. Only methods without parameters will be used.
     * <p>
     * Any method of the Pojo starting with:
     * <ul>
     * <li>is...</li>
     * <li>get...</li>
     * </ul>
     * will be automatically dissected and don't need to be added explicitly.
     *
     * @return a list of to-be-included method names
     */
    default List<String> fmIncludedMethods()
    {
        return List.of();
    }

    /**
     * Get a list of names for methods of this pojo to {@code exclude} in the dissection process.
     * <p>
     * Default is empty.
     * <p>
     * There is a number of globally blacklisted method names here {@link FlatMappableDissector#RAW_BLACKLIST}.
     * 
     * @return a list of to-be-excluded method names
     */
    default List<String> fmExcludedMethods()
    {
        return List.of();
    }

}
