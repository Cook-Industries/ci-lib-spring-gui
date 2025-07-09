/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTreeRoot
{

    private static final String JSON_FILE = "json-file";

    private String              handling;
    private PseudoElement       root;

    /**
     * Validates the state of this {@code JsonTreeRoot} object to ensure it can be successfully parsed.
     * <p>
     * This method checks that:
     * <ul>
     * <li>The {@code handling} property is not {@code null} or {@code empty}, and its value is either {@code static} or
     * {@code dynamic}</li>
     * <li>The {@code root} property is not {@code null}</li>
     * </ul>
     * <p>
     * If any validation fails, a {@code JsonParsingException} is thrown with relevant error details.
     *
     * @throws JsonParsingException if the this object state is invalid with a corresponding error message
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
