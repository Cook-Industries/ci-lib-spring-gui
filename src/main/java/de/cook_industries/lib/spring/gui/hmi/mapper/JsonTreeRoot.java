/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import de.cook_industries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTreeRoot
{

    private static final String JSON_FILE = "json-file";

    private String              handling;
    private PseudoElement       root;

    /**
     * Validates the state of the tree object to ensure it can be successfully parsed.<br>
     * 
     * This method checks that:<br>
     * - The handling property is not null or empty, and its value is either static or dynamic.<br>
     * - The root property is not null.<br>
     * 
     * If any validation fails, a {@code JsonParsingException} is thrown with relevant error details.
     *
     * @throws {@code JsonParsingException} if the tree object's state is invalid
     */
    public void validate()
    {
        if (handling == null || handling.isBlank())
        {
            throw new JsonParsingException(JSON_FILE, 0, 0, "tree.handling cannot be null/empty");
        }

        try
        {
            TreeHandling.valueOf(handling.toUpperCase());
        }
        catch (Exception ex)
        {
            throw new JsonParsingException(JSON_FILE, 0, 0, "tree.handling is not static/dynamic");
        }

        if (root == null)
        {
            throw new JsonParsingException(JSON_FILE, 0, 0, "tree.root cannot be null");
        }
    }
}
