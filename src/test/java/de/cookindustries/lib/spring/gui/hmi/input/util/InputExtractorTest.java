/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.cookindustries.lib.spring.gui.response.message.MessageTarget;
import de.cookindustries.lib.spring.gui.response.message.MessageType;

class InputExtractorTest
{

    private MultiValueMap<String, String> inputs;

    @BeforeEach
    public void init()
    {
        inputs = new LinkedMultiValueMap<>();
        inputs.add("__form_id", "testForm");
    }

    @Test
    void test_constructor_missing_form_id()
    {
        // setup
        inputs.clear();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> new InputExtractor(inputs));
    }

    @Test
    void test_constructor_useModalMessages()
    {
        // setup
        inputs.add("testKey", null);

        // run
        InputExtractor extractor = new InputExtractor(inputs, true);

        extractor.consumeString("testKey", s -> String.valueOf(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
        assertEquals(MessageTarget.MODAL, extractor.getMessages().get(0).getTarget());
    }

    @Test
    void test_constructor_consumeString()
    {
        // setup
        inputs.add("testKey", "testValue");
        List<String>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeString("testKey", s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals("testValue", result.get(0));
    }

    @Test
    void test_constructor_consumeString_unexpectedException()
    {
        // setup
        inputs.add("testKey", "testValue");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeString("testKey", s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }
}
