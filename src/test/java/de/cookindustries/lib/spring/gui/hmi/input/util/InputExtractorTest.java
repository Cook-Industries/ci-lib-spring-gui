/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import de.cookindustries.lib.spring.gui.response.message.MessageTarget;
import de.cookindustries.lib.spring.gui.response.message.MessageType;

class InputExtractorTest
{

    private enum TestEnum {
        VALUE1, VALUE2;
    }

    private MultiValueMap<String, String> inputs;

    @BeforeEach
    public void init()
    {
        inputs = new LinkedMultiValueMap<>();
        inputs.add("__form_id", "testForm");
    }

    @Test
    void test_constructor_missing_formId()
    {
        // setup
        inputs.clear();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> new InputExtractor(inputs));
    }

    @Test
    void test_constructor_empty_formId()
    {
        // setup
        inputs.clear();
        inputs.add("__form_id", "");

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> new InputExtractor(inputs));
    }

    @Test
    void test_constructor_useModalMessages()
    {
        // setup
        inputs.add("testKey", null);

        // run
        InputExtractor extractor = new InputExtractor(inputs, null, true);

        extractor.consumeString("testKey", s -> String.valueOf(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
        assertEquals(MessageTarget.MODAL, extractor.getMessages().get(0).getTarget());
    }

    @Test
    void test_invalidKey()
    {
        // setup
        inputs.add("testKey", "testValue");
        List<String>   result    = new ArrayList<>();

        // run & verify
        InputExtractor extractor = new InputExtractor(inputs);
        assertThrows(IllegalArgumentException.class, () -> extractor.consumeString(null, s -> result.add(s)));
        assertThrows(IllegalArgumentException.class, () -> extractor.consumeString("", s -> result.add(s)));
    }

    @Test
    void test_consumeString()
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
    void test_consumeString_unexpectedException()
    {
        // setup
        inputs.add("", "testValue");

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

    @Test
    void test_consumeString_withPattern_match()
    {
        // setup
        inputs.add("testKey", "test");
        List<String>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeString("testKey", "test", s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals("test", result.get(0));
    }

    @Test
    void test_consumeString_withPattern_noMatch()
    {
        // setup
        inputs.add("testKey", "test");
        List<String>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeString("testKey", "aaa", s -> result.add(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeInteger()
    {
        // setup
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeInteger("testKey", s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
    }

    @Test
    void test_consumeInteger_inBounds()
    {
        // setup
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeInteger("testKey", 0, 2, s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
    }

    @Test
    void test_consumeInteger_outBounds()
    {
        // setup
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeInteger("testKey", 2, 3, s -> result.add(s));
        extractor.consumeInteger("testKey", null, 3, s -> result.add(s));
        extractor.consumeInteger("testKey", 2, null, s -> result.add(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(3, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(1).getType());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(2).getType());
    }

    @Test
    void test_consumeInteger_NumberFormatException()
    {
        // setup
        inputs.add("testKey", "a");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeInteger("testKey", s -> s.toString());

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeInteger_unexpectedException()
    {
        // setup
        inputs.add("testKey", "1");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeInteger("testKey", s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeDouble()
    {
        // setup
        inputs.add("testKey", "1.0");
        List<Double>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeDouble("testKey", s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals(Double.valueOf(1.0), result.get(0));
    }

    @Test
    void test_consumeDouble_NumberFormatException()
    {
        // setup
        inputs.add("testKey", "a");
        List<Double>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeDouble("testKey", s -> result.add(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeDouble_unexpectedException()
    {
        // setup
        inputs.add("testKey", "1.0");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeDouble("testKey", s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeDate()
    {
        // setup
        inputs.add("testKey", "2025-11-02");
        List<Date>     result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeDate("testKey", s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals(Date.valueOf("2025-11-02"), result.get(0));
    }

    @Test
    void test_consumeDate_unexpectedException()
    {
        // setup
        inputs.add("testKey", "a");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeDate("testKey", s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeEnum()
    {
        // setup
        inputs.add("testKey", "value1");
        List<TestEnum> result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeEnum("testKey", TestEnum.class, s -> result.add(s));

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
        assertEquals(1, result.size());
        assertEquals(TestEnum.VALUE1, result.get(0));
    }

    @Test
    void test_consumeEnum_noSuchElement()
    {
        // setup
        inputs.add("testKey", "value3");
        List<TestEnum> result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeEnum("testKey", TestEnum.class, s -> result.add(s));

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_consumeEnum_unexpectedException()
    {
        // setup
        inputs.add("", "value1");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.consumeEnum("testKey", TestEnum.class, s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
        assertEquals(1, extractor.getMessages().size());
        assertEquals(MessageType.ERROR, extractor.getMessages().get(0).getType());
    }

    @Test
    void test_checkFiles_nullArray()
    {
        // setup

        // run
        InputExtractor extractor = new InputExtractor(inputs, null);

        // run & verify
        extractor.checkFiles(true, false);
        assertEquals(Boolean.FALSE, extractor.hasMessages());

        // run & verify
        extractor.checkFiles(false, false);
        assertEquals(Boolean.TRUE, extractor.hasMessages());
    }

    @Test
    void test_checkFiles_nullFile()
    {
        // setup

        // run
        InputExtractor extractor = new InputExtractor(inputs, Arrays.array());

        extractor.checkFiles(true, false);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
    }

    @Test
    void test_checkFiles_singleFile_allowed()
    {
        // setup
        MultipartFile  file      = new MockMultipartFile(
            "file",
            "testfile.txt",
            "text/plain",
            "Sample file content".getBytes(StandardCharsets.UTF_8));

        // run
        InputExtractor extractor = new InputExtractor(inputs, Arrays.array(file));

        extractor.checkFiles(false, false);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
    }

    @Test
    void test_checkFiles_MultipleFile_allowed()
    {
        // setup
        MultipartFile  file      = new MockMultipartFile(
            "file",
            "testfile.txt",
            "text/plain",
            "Sample file content".getBytes(StandardCharsets.UTF_8));

        // run
        InputExtractor extractor = new InputExtractor(inputs, Arrays.array(file, file));

        extractor.checkFiles(false, true);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMessages());
    }

    @Test
    void test_checkFiles_MultipleFile_notAllowed()
    {
        // setup
        MultipartFile  file      = new MockMultipartFile(
            "file",
            "testfile.txt",
            "text/plain",
            "Sample file content".getBytes(StandardCharsets.UTF_8));

        // run
        InputExtractor extractor = new InputExtractor(inputs, Arrays.array(file, file));

        extractor.checkFiles(false, false);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMessages());
    }

}
