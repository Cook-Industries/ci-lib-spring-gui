/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResourceLoaderTest
{

    @Test
    void test_exists_existingResource()
    {
        // setup
        String  path = "classpath:json-test-files/json-mapper/basic-root-static.ged";
        boolean result;

        // run
        result = ResourceLoader.exists(path);

        // verify
        assertTrue(result);
    }

    @Test
    void test_exists_missingResource()
    {
        // setup
        String  path = "config/does-not-exist.json";
        boolean result;

        // run
        result = ResourceLoader.exists(path);

        // verify
        assertFalse(result);
    }

}
