package de.cook_industries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cook_industries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;

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
