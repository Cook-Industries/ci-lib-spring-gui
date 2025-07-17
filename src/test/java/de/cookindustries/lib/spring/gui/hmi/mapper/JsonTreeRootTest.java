/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonTreeRoot;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.PseudoElement;

class JsonTreeRootTest
{

    @Test
    void test_validate_ok()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot(PseudoElement.builder().type("CONTENT").build());

        // run & verify
        assertDoesNotThrow(() -> root.validate());
    }

    @Test
    void test_validate_throws_rootNull()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot(null);

        // run & verify
        assertThrows(JsonParsingException.class, () -> root.validate());
    }
}
