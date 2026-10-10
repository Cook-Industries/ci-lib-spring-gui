/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AttributeTest
{

    @Test
    void test_attribute_withValue()
    {
        // setup
        String    input          = "test";
        String    value          = "value";
        String    expectedResult = "test=\"value\"";
        Attribute attribute      =
            Attribute
                .builder()
                .name(input)
                .value(value)
                .build();
        String    result;

        // run
        result = attribute.getHtmlRep();

        // verify
        assertEquals(expectedResult, result);
    }

    @Test
    void test_attribute_valueless()
    {
        // setup
        String    input          = "test";
        String    expectedResult = "test=\"test\"";
        Attribute attribute      =
            Attribute
                .builder()
                .name(input)
                .build();
        String    result;

        // run
        result = attribute.getHtmlRep();

        // verify
        assertEquals(expectedResult, result);
    }

    @Test
    void test_attribute_inactive()
    {
        // setup
        String    expectedResult = "";
        Attribute attribute      =
            Attribute
                .builder()
                .name("test")
                .active(false)
                .build();
        String    result;

        // run
        result = attribute.getHtmlRep();

        // verify
        assertEquals(expectedResult, result);
    }

}
