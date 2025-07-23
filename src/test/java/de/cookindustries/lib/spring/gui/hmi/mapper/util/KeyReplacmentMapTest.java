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
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.function.VoidCall;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;

class KeyReplacmentMapTest
{

    @Test
    void test_add_and_get()
    {
        // setup
        InputValueList inputValueList = new InputValueList();
        inputValueList.add(
            InputValue
                .builder()
                .text("test")
                .value("val")
                .build());

        // run
        KeywordReplacmentMap map =
            KeywordReplacmentMap.builder()
                .value("list", inputValueList)
                .value("integer", 1)
                .value("boolean", true)
                .value("string", "stuff")
                .clazz("class", "class")
                .function("function", new VoidCall())
                .build();

        // verify
        assertNotNull(map.getValue("list"));
        assertNotNull(map.getValue("integer"));
        assertNotNull(map.getValue("boolean"));
        assertNotNull(map.getValue("string"));
        assertNotNull(map.getClazz("class"));
        assertNotNull(map.getFunction("function"));
    }

    @Test
    void test_getPresedence()
    {
        // setup
        KeywordReplacmentMap map = KeywordReplacmentMap.builder().build();

        // run & verify
        assertEquals(Integer.valueOf(0), map.getPresedence());
    }

}
