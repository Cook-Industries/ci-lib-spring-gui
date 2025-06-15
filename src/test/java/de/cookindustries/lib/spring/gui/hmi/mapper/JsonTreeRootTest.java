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

class JsonTreeRootTest
{

    @Test
    void test_validate_ok()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot("static", new PseudoElement());

        // run & verify
        assertDoesNotThrow(() -> root.validate());
    }

    @Test
    void test_validate_throws_handlingNull()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot(null, null);

        // run & verify
        assertThrows(JsonParsingException.class, () -> root.validate());
    }

    @Test
    void test_validate_throws_handlingEmtpty()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot("", null);

        // run & verify
        assertThrows(JsonParsingException.class, () -> root.validate());
    }

    @Test
    void test_validate_throws_handlingIllegal()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot("test", null);

        // run & verify
        assertThrows(JsonParsingException.class, () -> root.validate());
    }

    @Test
    void test_validate_throws_rootNull()
    {
        // setup
        JsonTreeRoot root = new JsonTreeRoot("static", null);

        // run & verify
        assertThrows(JsonParsingException.class, () -> root.validate());
    }
}
