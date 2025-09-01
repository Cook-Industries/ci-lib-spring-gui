/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

class FlatMappableDissectorTest
{

    private FlatMappableDissector flatMappableDissector;

    @BeforeEach
    public void init()
    {
        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider());
    }

    private class TestObject implements FlatMappable
    {

        public String getClassLoader()
        {
            return "should not be read";
        }
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
