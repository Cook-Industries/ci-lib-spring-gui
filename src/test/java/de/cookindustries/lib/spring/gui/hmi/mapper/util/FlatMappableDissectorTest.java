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

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

class FlatMappableDissectorTest
{

    private FlatMappableDissector flatMappableDissector;

    CiLibProperties               properties = new CiLibProperties();

    @BeforeEach
    void init()
    {
        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider(), properties);
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
    void test_dissect_valueTransfer()
    {
        // setup
        TestObject testObject = new TestObject();
        TokenMap   tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertEquals(String.valueOf(testObject.getStrVal()), tokenMap.getValue("strVal"));
        assertEquals(Integer.valueOf(testObject.getIntVal()), tokenMap.getValue("intVal"));
        assertEquals(Boolean.valueOf(testObject.isBoolVal()), tokenMap.getValue("boolVal"));
        assertNull(tokenMap.getValue("thisNot"));
        assertNull(tokenMap.getValue("classLoader"));
    }

    @Test
    void test_dissect_blacklist()
    {
        // setup
        TestObject testObject = new TestObject();
        TokenMap   tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertNotNull(tokenMap);
    }

    private class ExcludingTestObject implements FlatMappable
    {

        public String getFirst()
        {
            return "a";
        }

        public String getSecond()
        {
            return "b";
        }

        public String getHidden()
        {
            return "x";
        }

        public String getThird()
        {
            return "c";
        }

        public String getFourth()
        {
            return "d";
        }

        @Override
        public List<String> fmExcludedMethods()
        {
            return List.of("getHidden");
        }
    }

    @Test
    void test_dissect_excludedMethod_skipsOnlyThatMethod()
    {
        // setup
        ExcludingTestObject testObject = new ExcludingTestObject();
        TokenMap            tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertNull(tokenMap.getValue("hidden"));
        assertEquals("a", tokenMap.getValue("first"));
        assertEquals("b", tokenMap.getValue("second"));
        assertEquals("c", tokenMap.getValue("third"));
        assertEquals("d", tokenMap.getValue("fourth"));
    }

    private class CountingBase
    {

        private int calls = 0;

        public String getCounted()
        {
            calls++;

            return "v";
        }

        int calls()
        {
            return calls;
        }
    }

    private class CountingTestObject extends CountingBase implements FlatMappable
    {
    }

    @Test
    void test_dissect_inheritedGetter_invokedOnce()
    {
        // setup
        CountingTestObject testObject = new CountingTestObject();
        TokenMap           tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertEquals("v", tokenMap.getValue("counted"));
        assertEquals(1, testObject.calls());
    }

    private class NamingTestObject implements FlatMappable
    {

        public String issue()
        {
            return "i";
        }

        public String getaway()
        {
            return "g";
        }

        public String isText()
        {
            return "t";
        }

        public boolean isActive()
        {
            return true;
        }

        public Boolean isWrapped()
        {
            return Boolean.TRUE;
        }

        public String getValid()
        {
            return "v";
        }
    }

    @Test
    void test_dissect_namesWithoutBeanConvention_areSkipped()
    {
        // setup
        NamingTestObject testObject = new NamingTestObject();
        TokenMap         tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertNull(tokenMap.getValue("sue"));
        assertNull(tokenMap.getValue("away"));
        assertNull(tokenMap.getValue("text"));
        assertEquals(Boolean.TRUE, tokenMap.getValue("active"));
        assertEquals(Boolean.TRUE, tokenMap.getValue("wrapped"));
        assertEquals("v", tokenMap.getValue("valid"));
    }

    private class IncludingTestObject implements FlatMappable
    {

        public String issue()
        {
            return "i";
        }

        @Override
        public List<String> fmIncludedMethods()
        {
            return List.of("issue");
        }
    }

    @Test
    void test_dissect_includedMethodWithGetterLikePrefix_keepsFullName()
    {
        // setup
        IncludingTestObject testObject = new IncludingTestObject();
        TokenMap            tokenMap;

        // run
        tokenMap = flatMappableDissector.dissect(testObject, 0, Locale.ENGLISH);

        // verify
        assertEquals("i", tokenMap.getValue("issue"));
        assertNull(tokenMap.getValue("sue"));
    }
}
