/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.KeyAlreadyInUseException;
import de.cookindustries.lib.spring.gui.util.exception.ObjectSealedException;

class KeyReplacmentMapTest
{

    private KeyReplacmentMap map;

    @BeforeEach
    public void init()
    {
        map = new KeyReplacmentMap();
    }

    @Test
    void test_sealed()
    {
        // setup

        // run
        map.seal();

        // verify
        assertThrows(ObjectSealedException.class, () -> map.value("test", true));
    }

    @Test
    void test_keyAlreadyInUse()
    {
        // setup
        map.value("integer", 1);

        // run & verify
        assertThrows(KeyAlreadyInUseException.class, () -> map.value("integer", "2"));
        assertNotNull(map.getValue("integer"));
        assertEquals(Integer.class, map.getValue("integer").getClass());
        assertEquals(Integer.valueOf(1), map.getValue("integer"));
    }

    @Test
    void test_adds()
    {
        // setup
        InputValueList inputValueList = new InputValueList();
        inputValueList.add(InputValue.builder().text("test").value("val").build());

        // run
        map.value("list", inputValueList);
        map.value("integer", 1);
        map.value("boolean", true);
        map.value("string", "stuff");

        // verify
        assertNotNull(map.getValue("list"));
        assertNotNull(map.getValue("integer"));
        assertNotNull(map.getValue("boolean"));
        assertNotNull(map.getValue("string"));
    }

    @Test
    void test_getPresedence()
    {
        // setup

        // run & verify
        assertEquals(Integer.valueOf(0), map.getPresedence());
    }

}
