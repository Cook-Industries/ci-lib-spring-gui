/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
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
    public void init()
    {
        stringConcat = new StringConcat();
    }

    @Test
    void test_nl()
    {
        // setup

        // run
        stringConcat.nl();
        String result = stringConcat.toString();

        // verify
        assertEquals(NL, result);
    }

    @Test
    void test_append_string()
    {
        // setup
        String value = "test";

        // run
        stringConcat.append(value);
        String result = stringConcat.toString();

        // verify
        assertEquals(value, result);
    }

    @Test
    void test_append_string_null()
    {
        // setup
        String value = null;

        // run
        stringConcat.append(value);
        String result = stringConcat.toString();

        // verify
        assertEquals("null", result);
    }

    @Test
    void test_appendnl_string()
    {
        // setup
        String value = "test";

        // run
        stringConcat.appendnl(value);
        String result = stringConcat.toString();

        // verify
        assertEquals(value + NL, result);
    }

    @Test
    void test_append_string_withCondition_false()
    {
        // setup
        String value = "test";

        // run
        stringConcat.append(false, value);
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_append_string_withCondition_true()
    {
        // setup
        String value = "test";

        // run
        stringConcat.append(true, value);
        String result = stringConcat.toString();

        // verify
        assertEquals("test", result);
    }

    @Test
    void test_appendnl_string_withCondition_false()
    {
        // setup
        String value = "test";

        // run
        stringConcat.appendnl(false, value);
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_appendnl_string_withCondition_true()
    {
        // setup
        String value = "test";

        // run
        stringConcat.appendnl(true, value);
        String result = stringConcat.toString();

        // verify
        assertEquals("test" + NL, result);
    }

    @Test
    void test_append_supplier_withCondition_false()
    {
        // setup
        Supplier<String> supplier = () -> "test";

        // run
        stringConcat.append(false, supplier);
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_append_supplier_withCondition_true()
    {
        // setup
        Supplier<String> supplier = () -> "test";

        // run
        stringConcat.append(true, supplier);
        String result = stringConcat.toString();

        // verify
        assertEquals("test", result);
    }

    @Test
    void test_appendnl_supplier_withCondition_false()
    {
        // setup
        Supplier<String> supplier     = () -> "test";
        Supplier<String> nullSupplier = null;

        // run
        stringConcat.appendnl(false, supplier);
        stringConcat.appendnl(false, nullSupplier);
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_appendnl_supplier_withCondition_true()
    {
        // setup
        Supplier<String> supplier     = () -> "test";
        Supplier<String> nullSupplier = null;

        // run
        stringConcat.appendnl(true, supplier);
        stringConcat.appendnl(true, nullSupplier);
        String result = stringConcat.toString();

        // verify
        assertEquals("test" + NL, result);
    }

    @Test
    void test_append_enum()
    {
        // setup

        // run
        stringConcat.append(TestEnum.TEST);
        String result = stringConcat.toString();

        // verify
        assertEquals("TEST", result);
    }

    @Test
    void test_appendnl_enum()
    {
        // setup

        // run
        stringConcat.appendnl(TestEnum.TEST);
        String result = stringConcat.toString();

        // verify
        assertEquals("TEST" + NL, result);
    }

    @Test
    void test_append_character()
    {
        // setup
        Character value = Character.valueOf('a');

        // run
        stringConcat.append(value);
        String result = stringConcat.toString();

        // verify
        assertEquals("a", result);
    }

    @Test
    void test_appendnl_character()
    {
        // setup
        Character value = Character.valueOf('a');

        // run
        stringConcat.appendnl(value);
        String result = stringConcat.toString();

        // verify
        assertEquals(value + NL, result);
    }

    @Test
    void test_append_integer()
    {
        // setup
        Integer value = Integer.valueOf(1);

        // run
        stringConcat.append(value);
        String result = stringConcat.toString();

        // verify
        assertEquals("1", result);
    }

    @Test
    void test_appendnl_integer()
    {
        // setup
        Integer value = Integer.valueOf(1);

        // run
        stringConcat.appendnl(value);
        String result = stringConcat.toString();

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

        // run
        stringConcat.append(list, separator);
        String result = stringConcat.toString();

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

        // run
        stringConcat.append(list, separator);
        stringConcat.append(nullList, separator);
        stringConcat.append(list, null);
        String result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_append_objects()
    {
        // setup
        Object[] objects   = {"a", "b"};
        String   separator = "-";

        // run
        stringConcat.append(objects, separator);
        String result = stringConcat.toString();

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

        // run
        stringConcat.append(objects, separator);
        stringConcat.append(nullObjects, separator);
        stringConcat.append(objects, null);
        String result = stringConcat.toString();

        // verify
        assertEquals("a-b", result);
    }

    @Test
    void test_appendnl_strings()
    {
        // setup
        String[] strings = {"a", "b"};

        // run
        stringConcat.appendnl(strings);
        String result = stringConcat.toString();

        // verify
        assertEquals("a" + NL + "b" + NL, result);
    }

    @Test
    void test_removeLastChars()
    {
        // setup
        String string = "a";

        // run
        stringConcat.append(string);
        stringConcat.removeLastChars(1);
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_clear()
    {
        // setup
        String string = "a";

        // run
        stringConcat.append(string);
        stringConcat.clear();
        String result = stringConcat.toString();

        // verify
        assertEquals("", result);
    }

    @Test
    void test_isEmpty()
    {
        // setup

        // run

        // verify
        assertEquals(true, stringConcat.isEmpty());
    }

    @Test
    void test_size()
    {
        // setup
        String string = "a";

        // run
        stringConcat.append(string);

        // verify
        assertEquals(1, stringConcat.size());
    }
}
