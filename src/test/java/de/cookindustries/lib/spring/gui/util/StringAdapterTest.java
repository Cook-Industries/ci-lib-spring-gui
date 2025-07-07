/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

class StringAdapterTest
{

    @Test
    void test_prefix_string()
    {
        // setup
        String prefix  = "* ";
        String content = "test";

        // run
        String result  = StringAdapter.prefix(prefix, content);

        // verify
        assertEquals(prefix + content, result);
    }

    @Test
    void test_suffix_string()
    {
        // setup
        String suffix  = " *";
        String content = "test";

        // run
        String result  = StringAdapter.suffix(content, suffix);

        // verify
        assertEquals(content + suffix, result);
    }

    @Test
    void test_prefixAndSuffix_string()
    {
        // setup
        String prefix  = "* ";
        String suffix  = " *";
        String content = "test";

        // run
        String result  = StringAdapter.prefixAndSuffix(prefix, content, suffix);

        // verify
        assertEquals(prefix + content + suffix, result);
    }

    @Test
    void test_prefix_collection()
    {
        // setup
        String             prefix     = "* ";
        String             content    = "test";
        Collection<String> list       = List.of(content);
        Collection<String> nullList   = null;

        // run
        Collection<String> result     = StringAdapter.prefix(list, prefix);
        Collection<String> nullResult = StringAdapter.prefix(nullList, prefix);

        // verify
        assertEquals(1, result.size());
        assertEquals(prefix + content, ((List<String>) result).get(0));
        assertEquals(0, nullResult.size());
    }

    @Test
    void test_suffix_collection()
    {
        // setup
        String             suffix     = " *";
        String             content    = "test";
        Collection<String> list       = List.of(content);
        Collection<String> nullList   = null;

        // run
        Collection<String> result     = StringAdapter.suffix(list, suffix);
        Collection<String> nullResult = StringAdapter.suffix(nullList, suffix);

        // verify
        assertEquals(1, result.size());
        assertEquals(content + suffix, ((List<String>) result).get(0));
        assertEquals(0, nullResult.size());
    }

    @Test
    void test_prefixAndSuffix_collection()
    {
        // setup
        String             prefix     = "* ";
        String             suffix     = " *";
        String             content    = "test";
        Collection<String> list       = List.of(content);
        Collection<String> nullList   = null;

        // run
        Collection<String> result     = StringAdapter.prefixAndSuffix(list, prefix, suffix);
        Collection<String> nullResult = StringAdapter.prefixAndSuffix(nullList, prefix, suffix);

        // verify
        assertEquals(1, result.size());
        assertEquals(prefix + content + suffix, ((List<String>) result).get(0));
        assertEquals(0, nullResult.size());
    }

    @Test
    void test_separate_collection()
    {
        // setup
        String             separator  = " * ";
        String             content    = "test";
        Collection<String> list       = List.of(content, content);
        Collection<String> nullList   = null;

        // run
        String             result     = StringAdapter.separate(list, separator);
        String             nullResult = StringAdapter.separate(nullList, separator);

        // verify
        assertEquals(content + separator + content, result);
        assertEquals("", nullResult);
    }

    @Test
    void test_separate_array()
    {
        // setup
        String   separator  = " * ";
        String   content    = "test";
        Object[] array      = new Object[] {content, content};
        Object[] nullArray  = null;

        // run
        String   result     = StringAdapter.separate(separator, array);
        String   nullResult = StringAdapter.separate(separator, nullArray);

        // verify
        assertEquals(content + separator + content, result);
        assertEquals("", nullResult);
    }

    @Test
    void test_from_collection()
    {
        // setup
        String             content    = "test";
        Collection<String> list       = List.of(content, content);
        Collection<String> nullList   = null;

        // run
        String             result     = StringAdapter.from(list);
        String             nullResult = StringAdapter.from(nullList);

        // verify
        assertEquals(content + content, result);
        assertEquals("", nullResult);
    }

    @Test
    void test_from_array()
    {
        // setup
        String   content    = "test";
        Object[] array      = new Object[] {content, content};
        Object[] nullArray  = null;

        // run
        String   result     = StringAdapter.from(array);
        String   nullResult = StringAdapter.from(nullArray);

        // verify
        assertEquals(content + content, result);
        assertEquals("", nullResult);
    }

}
