/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
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
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.InputExtractionException;

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
    void test_invalidKey()
    {
        // setup
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkAndConsumeAsString(null, result::add));
        assertThrows(IllegalArgumentException.class, () -> extractor.checkAndConsumeAsString("", result::add));
    }

    @Test
    void test_approve()
    {
        // setup
        inputs.add("testKey", "");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsNotEmptyString("testKey", result::add);

        // verify
        assertThrows(InputExtractionException.class, () -> extractor.approve());
    }

    @Test
    void test_checkAndConsumeAsString()
    {
        // setup
        inputs.add("testKey", "testValue");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsString("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals("testValue", result.get(0));
    }

    @Test
    void test_checkAndConsumeAsString_withPattern_match()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("test"))
                .build();
        inputs.add("testKey", "test");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsString("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals("test", result.get(0));
    }

    @Test
    void test_checkAndConsumeAsString_withPattern_noMatch()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("aaa"))
                .build();
        inputs.add("testKey", "test");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsString("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.INVALID, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsNotEmptyString_pass()
    {
        // setup
        inputs.add("testKey", "testValue");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsNotEmptyString("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals("testValue", result.get(0));
    }

    @Test
    void test_checkAndConsumeAsNotEmptyString_noPass()
    {
        // setup
        inputs.add("testKey", "");
        List<String>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsNotEmptyString("testKey", result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.INVALID, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsBooleanDefaultFalse()
    {
        // setup
        inputs.add("testKey", "");
        inputs.add("bool", "true");
        List<Boolean>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsBooleanDefaultFalse("testKey", result::add);
        extractor.checkAndConsumeAsBooleanDefaultFalse("bool", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(0, extractor.getMarker().size());
        assertEquals(Boolean.FALSE, result.get(0));
        assertEquals(Boolean.TRUE, result.get(1));
    }

    @Test
    void test_checkAndConsumeAsBooleanDefaultTrue()
    {
        // setup
        inputs.add("testKey", "");
        inputs.add("bool", "false");
        List<Boolean>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsBooleanDefaultTrue("testKey", result::add);
        extractor.checkAndConsumeAsBooleanDefaultTrue("bool", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(0, extractor.getMarker().size());
        assertEquals(Boolean.TRUE, result.get(0));
        assertEquals(Boolean.FALSE, result.get(1));
    }

    @Test
    void test_checkAndConsumeAsInteger()
    {
        // setup
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsInteger("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
    }

    @Test
    void test_checkAndConsumeAsInteger_NumberFormatException()
    {
        // setup
        inputs.add("testKey", "a");
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsInteger("testKey", s -> s.toString());

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.NOT_PARSABLE, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsInteger_inBounds()
    {
        // setup
        IntegerInputProcessor processor = IntegerInputProcessor
            .builder()
            .lowerBound(0)
            .upperBound(2)
            .build();
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsInteger("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
    }

    @Test
    void test_checkAndConsumeAsInteger_outOfBounds()
    {
        // setup
        IntegerInputProcessor processor;
        inputs.add("testKey", "1");
        List<Integer>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        processor = IntegerInputProcessor
            .builder()
            .lowerBound(2)
            .upperBound(2)
            .build();
        extractor.checkAndConsumeAsInteger("testKey", processor, result::add);

        processor = IntegerInputProcessor
            .builder()
            .lowerBound(3)
            .upperBound(2)
            .build();
        extractor.checkAndConsumeAsInteger("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(2, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.OUT_OF_RANGE, extractor.getMarker().get(0).getType());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(1).getCategory());
        assertEquals(MarkerType.OUT_OF_RANGE, extractor.getMarker().get(1).getType());
    }

    @Test
    void test_checkAndConsumeAsDouble()
    {
        // setup
        inputs.add("testKey", "1.0");
        List<Double>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsDouble("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(Double.valueOf(1.0), result.get(0));
    }

    @Test
    void test_checkAndConsumeAsDouble_NumberFormatException()
    {
        // setup
        inputs.add("testKey", "a");
        List<Double>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsDouble("testKey", result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.NOT_PARSABLE, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsDouble_inBounds()
    {
        // setup
        DoubleInputProcessor processor = DoubleInputProcessor
            .builder()
            .lowerBound(0d)
            .upperBound(2d)
            .build();
        inputs.add("testKey", "1.0");
        List<Double>   result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsDouble("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(Double.valueOf(1.0d), result.get(0));
    }

    @Test
    void test_checkAndConsumeAsDouble_outOfBounds()
    {
        // setup
        DoubleInputProcessor processor;
        inputs.add("testKey", "1");
        List<Double>   result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        processor = DoubleInputProcessor
            .builder()
            .lowerBound(2d)
            .upperBound(3d)
            .build();
        extractor.checkAndConsumeAsDouble("testKey", processor, result::add);

        processor = DoubleInputProcessor
            .builder()
            .lowerBound(3d)
            .upperBound(2d)
            .build();
        extractor.checkAndConsumeAsDouble("testKey", processor, result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(2, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.OUT_OF_RANGE, extractor.getMarker().get(0).getType());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(1).getCategory());
        assertEquals(MarkerType.OUT_OF_RANGE, extractor.getMarker().get(1).getType());
    }

    @Test
    void test_checkAndConsumeAsDate()
    {
        // setup
        inputs.add("testKey", "2025-11-02");
        List<Date>     result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.checkAndConsumeAsDate("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(Date.valueOf("2025-11-02"), result.get(0));
    }

    @Test
    void test_checkAndConsumeAsDate_notParsable()
    {
        // setup
        inputs.add("testKey", "a");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.checkAndConsumeAsDate("testKey", s -> {
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.NOT_PARSABLE, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsEnum()
    {
        // setup
        inputs.add("testKey", "value1");
        List<TestEnum> result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.checkAndConsumeAsEnum("testKey", TestEnum.class, result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(TestEnum.VALUE1, result.get(0));
    }

    @Test
    void test_checkAndConsumeAsEnum_noSuchElement()
    {
        // setup
        inputs.add("testKey", "value3");
        List<TestEnum> result    = new ArrayList<>();

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.checkAndConsumeAsEnum("testKey", TestEnum.class, result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.NOT_PARSABLE, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsEnum_unexpectedException()
    {
        // setup
        inputs.add("", "value1");

        // run
        InputExtractor extractor = new InputExtractor(inputs);

        extractor.checkAndConsumeAsEnum("testKey", TestEnum.class, s -> {
            throw new IllegalArgumentException();
        });

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(1, extractor.getMarker().size());
        assertEquals(MarkerCategory.ERROR, extractor.getMarker().get(0).getCategory());
        assertEquals(MarkerType.NO_VALUE, extractor.getMarker().get(0).getType());
    }

    @Test
    void test_checkAndConsumeAsTagList()
    {
        // setup
        inputs.add("testKey", "[{\"value\":\"tag1\"}]");
        List<TagList>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsTagList("testKey", result::add);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(new Tag("tag1"), result.get(0).get(0));
    }

    @Test
    void test_checkAndConsumeAsNotEmptyTagList()
    {
        // setup
        inputs.add("testKey", "[]");
        List<TagList>  result    = new ArrayList<>();
        InputExtractor extractor = new InputExtractor(inputs);

        // run
        extractor.checkAndConsumeAsNotEmptyTagList("testKey", result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(0, result.size());
    }

    @Test
    void test_checkFiles_invalidKey()
    {
        // setup
        InputExtractor extractor = new InputExtractor(inputs);

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles(null, false, false));
        assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles("", false, false));
    }

    @Test
    void test_checkFiles_nullArray()
    {
        // setup
        InputExtractor extractor = new InputExtractor(inputs);

        // run & verify
        extractor.checkFiles("files", true, false);
        assertEquals(Boolean.FALSE, extractor.hasMarker());

        // run & verify
        extractor.checkFiles("files", false, false);
        assertEquals(Boolean.TRUE, extractor.hasMarker());
    }

    @Test
    void test_checkFiles_nullFile()
    {
        // setup
        InputExtractor extractor = new InputExtractor(inputs, new MultipartFile[] {});

        // run
        extractor.checkFiles("files", true, false);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
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
        InputExtractor extractor = new InputExtractor(inputs, new MultipartFile[] {file});

        extractor.checkFiles("files", false, false);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
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
        InputExtractor extractor = new InputExtractor(inputs, new MultipartFile[] {file, file});

        extractor.checkFiles("files", false, true);

        // verify
        assertEquals(Boolean.FALSE, extractor.hasMarker());
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
        InputExtractor extractor = new InputExtractor(inputs, new MultipartFile[] {file, file});

        extractor.checkFiles("files", false, false);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
    }

}
