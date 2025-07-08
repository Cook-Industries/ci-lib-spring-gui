/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public abstract class AbsFunctionArgs
{

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public final String toString()
    {
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
