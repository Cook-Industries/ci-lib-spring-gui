/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonTreeMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonTreeRoot;

class JsonTreeMapperTest
{

    private static final String PATH = "json-test-files/empty_root.json";

    private JsonTreeMapper      mapper;

    @BeforeEach
    void init()
    {
        mapper = new JsonTreeMapper();
    }

    @Test
    void test_map_with_resource()
    {
        // setup

        // run
        JsonTreeRoot treeRoot = mapper.map(PATH);

        // verify
        assertNotNull(treeRoot);
    }

    @Test
    void test_map_with_nullResource()
    {
        // setup

        // run & verify
        assertThrows(JsonMapperException.class, () -> mapper.map(""));
    }

    @Test
    void test_map_with_file() throws URISyntaxException
    {
        // setup
        URL          resource = getClass().getClassLoader().getResource(PATH);
        File         file     = new File(resource.toURI());

        // run
        JsonTreeRoot treeRoot = mapper.map(file);

        // verify
        assertNotNull(treeRoot);
    }

    @Test
    void test_map_with_nullFile()
    {
        // setup
        File file = new File("");

        // run & verify
        assertThrows(JsonMapperException.class, () -> mapper.map(file));
    }

}
