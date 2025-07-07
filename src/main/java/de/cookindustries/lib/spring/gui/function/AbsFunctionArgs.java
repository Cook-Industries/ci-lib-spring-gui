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
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException ex)
        {
            throw new IllegalArgumentException(ex);
        }
    }
}
