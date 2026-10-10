/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringConcatTest
{

    private enum TestEnum {
        TEST;
    }

    private static final String NL = System.lineSeparator();

    private StringConcat        stringConcat;

    @BeforeEach
    void init()
    {
        stringConcat = new StringConcat();
    }

    @Test
    void test_nl_newline()
    {
        // setup
        String result;
        // run
        stringConcat.nl();
        result = stringConcat.toString();

        // verify
        assertEquals(NL, result);
    }

    @Test
    void test_append_string()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.append(value);
        result = stringConcat.toString();

        // verify
        assertEquals(value, result);
    }

    @Test
    void test_append_string_null()
    {
        // setup
        String value = null;
        String result;

        // run
        stringConcat.append(value);
        result = stringConcat.toString();

        // verify
        assertEquals("null", result);
    }

    @Test
    void test_appendnl_string()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.appendnl(value);
        result = stringConcat.toString();

        // verify
        assertEquals(value + NL, result);
    }

    @Test
    void test_append_string_withCondition_false()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.append(false, value);
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_append_string_withCondition_true()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.append(true, value);
        result = stringConcat.toString();

        // verify
        assertEquals("test", result);
    }

    @Test
    void test_appendnl_string_withCondition_false()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.appendnl(false, value);
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_appendnl_string_withCondition_true()
    {
        // setup
        String value = "test";
        String result;

        // run
        stringConcat.appendnl(true, value);
        result = stringConcat.toString();

        // verify
        assertEquals("test" + NL, result);
    }

    @Test
    void test_append_supplier_withCondition_false()
    {
        // setup
        Supplier<String> supplier = () -> "test";
        String           result;

        // run
        stringConcat.append(false, supplier);
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_append_supplier_withCondition_true()
    {
        // setup
        Supplier<String> supplier = () -> "test";
        String           result;

        // run
        stringConcat.append(true, supplier);
        result = stringConcat.toString();

        // verify
        assertEquals("test", result);
    }

    @Test
    void test_appendnl_supplier_withCondition_false()
    {
        // setup
        Supplier<String> supplier     = () -> "test";
        Supplier<String> nullSupplier = null;
        String           result;

        // run
        stringConcat.appendnl(false, supplier);
        stringConcat.appendnl(false, nullSupplier);
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_appendnl_supplier_withCondition_true()
    {
        // setup
        Supplier<String> supplier     = () -> "test";
        Supplier<String> nullSupplier = null;
        String           result;

        // run
        stringConcat.appendnl(true, supplier);
        stringConcat.appendnl(true, nullSupplier);
        result = stringConcat.toString();

        // verify
        assertEquals("test" + NL, result);
    }

    @Test
    void test_append_enum()
    {
        // setup
        String result;
        // run
        stringConcat.append(TestEnum.TEST);
        result = stringConcat.toString();

        // verify
        assertEquals("TEST", result);
    }

    @Test
    void test_appendnl_enum()
    {
        // setup
        String result;
        // run
        stringConcat.appendnl(TestEnum.TEST);
        result = stringConcat.toString();

        // verify
        assertEquals("TEST" + NL, result);
    }

    @Test
    void test_append_character()
    {
        // setup
        Character value = Character.valueOf('a');
        String    result;

        // run
        stringConcat.append(value);
        result = stringConcat.toString();

        // verify
        assertEquals("a", result);
    }

    @Test
    void test_appendnl_character()
    {
        // setup
        Character value = Character.valueOf('a');
        String    result;

        // run
        stringConcat.appendnl(value);
        result = stringConcat.toString();

        // verify
        assertEquals(value + NL, result);
    }

    @Test
    void test_append_integer()
    {
        // setup
        Integer value = Integer.valueOf(1);
        String  result;

        // run
        stringConcat.append(value);
        result = stringConcat.toString();

        // verify
        assertEquals("1", result);
    }

    @Test
    void test_appendnl_integer()
    {
        // setup
        Integer value = Integer.valueOf(1);
        String  result;

        // run
        stringConcat.appendnl(value);
        result = stringConcat.toString();

        // verify
        assertEquals(value + NL, result);
    }

    @Test
    void test_append_collection()
    {
        // setup
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        String separator = "-";
        String result;

        // run
        stringConcat.append(list, separator);
        result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_append_collection_nulls()
    {
        // setup
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        String       separator = "-";
        List<String> nullList  = null;
        String       result;

        // run
        stringConcat.append(list, separator);
        stringConcat.append(nullList, separator);
        stringConcat.append(list, null);
        result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_append_objects()
    {
        // setup
        Object[] objects   = {"a", "b"};
        String   separator = "-";
        String   result;

        // run
        stringConcat.append(objects, separator);
        result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_append_objects_nulls()
    {
        // setup
        Object[] objects     = {"a", "b"};
        String   separator   = "-";
        Object[] nullObjects = null;
        String   result;

        // run
        stringConcat.append(objects, separator);
        stringConcat.append(nullObjects, separator);
        stringConcat.append(objects, null);
        result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_appendnl_strings()
    {
        // setup
        String[] strings = {"a", "b"};
        String   result;

        // run
        stringConcat.appendnl(strings);
        result = stringConcat.toString();

        // verify
        assertEquals("a" + NL + "b" + NL, result);
    }

    @Test
    void test_removeLastChars_single()
    {
        // setup
        String string = "a";
        String result;

        // run
        stringConcat.append(string);
        stringConcat.removeLastChars(1);
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_clear_afterAppend()
    {
        // setup
        String string = "a";
        String result;

        // run
        stringConcat.append(string);
        stringConcat.clear();
        result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_isEmpty_fresh()
    {
        // setup
        boolean result;

        // run
        result = stringConcat.isEmpty();

        // verify
        assertEquals(true, result);
    }

    @Test
    void test_size_afterAppend()
    {
        // setup
        String string = "a";
        int    result;

        // run
        stringConcat.append(string);
        result = stringConcat.size();

        // verify
        assertEquals(1, result);
    }
}
