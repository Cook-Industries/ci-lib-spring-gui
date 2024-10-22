/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import com.ci.lib.spring.web.hmi.mapper.exception.JsonParsingException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTreeRoot
{

    private String        handling;
    private String        name;
    private PseudoElement root;

    /**
     * Validate this {@ JsonTreeRoot} to verify all necessary fields are existant
     */
    public void validate()
    {
        if (handling == null || handling.isBlank())
        {
            throw new JsonParsingException(null, 0, 0, "tree.handling cannot be null/empty");
        }

        try
        {
            TreeHandling.valueOf(handling.toUpperCase());
        }
        catch (Exception ex)
        {
            throw new JsonParsingException(null, 0, 0, "tree.handling is not static/dynamic");
        }

        if (name == null || name.isBlank())
        {
            throw new JsonParsingException(null, 0, 0, "tree.name cannot be null/empty");
        }

        if (root == null)
        {
            throw new JsonParsingException(null, 0, 0, "tree.root cannot be null");
        }
    }
}
