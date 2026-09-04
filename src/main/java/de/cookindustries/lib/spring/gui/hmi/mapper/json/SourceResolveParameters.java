/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.Locale;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableList;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;

/**
 * Record for source/repetition resolve inside {@code JsonMapper}.
 * 
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
record SourceResolveParameters(int repetitions, boolean sourceExpected, boolean listSource, FlatMappable target,
    FlatMappableList<?> targetList)
{

    public TokenMap resolveToTempTokenMap(FlatMappableDissector flatMappableDissector, int repetiton, int depth, Locale locale)
    {
        if (targetList != null)
        {
            return flatMappableDissector.dissect(targetList.get(repetiton), depth, locale);
        }
        else if (target != null)
        {
            return flatMappableDissector.dissect(target, depth, locale);
        }

        if (sourceExpected)
        {
            throw new JsonMapperException("could not resolve targetList/target for element");
        }

        return null;
    }
}
