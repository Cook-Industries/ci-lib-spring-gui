/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Arguments that should be transmitted with a {@link AbsFunctionCall}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public interface AbsFunctionArgs
{

    default String toHtmlString()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            return objectMapper.writeValueAsString(this).replace("\"", "&quot;");
        }
        catch (JsonProcessingException ex)
        {
            throw new IllegalArgumentException(ex);
        }
    }
}
