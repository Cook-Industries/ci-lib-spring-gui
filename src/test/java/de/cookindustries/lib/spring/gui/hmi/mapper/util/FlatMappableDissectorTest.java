/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

class FlatMappableDissectorTest
{

    private FlatMappableDissector flatMappableDissector;

    @BeforeEach
    void init()
    {
        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider());
    }

    private class TestObject implements FlatMappable
    {

        private final String  strVal  = "test-value";
        private final int     intVal  = -156;
        private final boolean boolVal = true;

        public String getStrVal()
        {
            return strVal;
        }

        public int getIntVal()
        {
            return intVal;
        }

        public boolean isBoolVal()
        {
            return boolVal;
        }

        @SuppressWarnings("unused")
        public boolean getThisNot(boolean b)
        {
            return b;
        }

        @SuppressWarnings("unused")
        public String getClassLoader()
        {
            throw new IllegalAccessError();
        }
    }

    @Test
    void test_valueTransfer()
    {
        // setup
        TestObject testObject = new TestObject();

        // run
        TokenMap   tokenMap   = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertEquals(String.valueOf(testObject.getStrVal()), tokenMap.getValue("strVal"));
        assertEquals(Integer.valueOf(testObject.getIntVal()), tokenMap.getValue("intVal"));
        assertEquals(Boolean.valueOf(testObject.isBoolVal()), tokenMap.getValue("boolVal"));
        assertNull(tokenMap.getValue("thisNot"));
        assertNull(tokenMap.getValue("classLoader"));
    }

    @Test
    void test_blacklist()
    {
        // setup
        TestObject testObject = new TestObject();

        // run
        TokenMap tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertNotNull(tokenMap);
    }
}
