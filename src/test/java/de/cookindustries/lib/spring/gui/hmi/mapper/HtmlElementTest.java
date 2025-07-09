/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.mapper.html.Attribute;
import de.cookindustries.lib.spring.gui.hmi.mapper.html.HtmlElement;

class HtmlElementTest
{

    private static final String TEST_VAL = "test";

    @Test
    void test_html_singleTagNoClass()
    {
        // setup
        String      expected = "<div checked class=\"\" data-test=\"test\">";
        HtmlElement element  = HtmlElement
            .builder()
            .tag("div")
            .attribute(new Attribute("checked", true))
            .dataAttribute(TEST_VAL, TEST_VAL)
            .isSingleTag(true)
            .build();

        // run
        String      html     = element.html();

        // verify
        assertEquals(expected, html);
    }

    @Test
    void test_html_fullElement()
    {
        // setup
        String      expected = "<div checked=\"test\" class=\"test\" data-test=\"test\">test</div>";
        HtmlElement element  = HtmlElement
            .builder()
            .tag("div")
            .attribute(new Attribute("checked", TEST_VAL))
            .clazz(TEST_VAL)
            .dataAttribute(TEST_VAL, TEST_VAL)
            .isSingleTag(false)
            .content(TEST_VAL)
            .build();

        // run
        String      html     = element.html();

        // verify
        assertEquals(expected, html);
    }
}
