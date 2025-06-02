package de.cook_industries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cook_industries.lib.spring.gui.hmi.input.util.InputValue;
import de.cook_industries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cook_industries.lib.spring.gui.hmi.mapper.exception.ValueMapKeyAlreadyInUseException;
import de.cook_industries.lib.spring.gui.util.exception.ObjectSealedException;

class ValueMapTest
{

    private ValueMap map;

    @BeforeEach
    public void init()
    {
        map = new ValueMap();
    }

    @Test
    void test_sealed()
    {
        // setup

        // run
        map.seal();

        // verify
        assertThrows(ObjectSealedException.class, () -> map.add("test", true));
    }

    @Test
    void test_keyAlreadyInUse()
    {
        // setup
        map.add("integer", 1);

        // run & verify
        assertThrows(ValueMapKeyAlreadyInUseException.class, () -> map.add("integer", "2"));
        assertNotNull(map.get("integer"));
        assertEquals(Integer.class, map.get("integer").getClass());
        assertEquals(Integer.valueOf(1), map.get("integer"));
    }

    @Test
    void test_adds()
    {
        // setup
        InputValueList inputValueList = new InputValueList();
        inputValueList.add(InputValue.builder().text("test").value("val").build());

        // run
        map.add("list", inputValueList);
        map.add("integer", 1);
        map.add("boolean", true);
        map.add("string", "stuff");

        // verify
        assertNotNull(map.get("list"));
        assertNotNull(map.get("integer"));
        assertNotNull(map.get("boolean"));
        assertNotNull(map.get("string"));
    }

    @Test
    void test_getPresedence()
    {
        // setup

        // run & verify
        assertEquals(Integer.valueOf(0), map.getPresedence());
    }

}
