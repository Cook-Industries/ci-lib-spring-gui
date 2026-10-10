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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class InputExtractorTest
{

    private static Consumer<List<String>> HANDLER = list -> {
    };

    private enum TestEnum
    {
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
        assertThrows(IllegalArgumentException.class, () -> new InputExtractor<Object>(null, inputs, Locale.ENGLISH, () -> null));
    }

    @Test
    void test_constructor_empty_formId()
    {
        // setup
        inputs.clear();
        inputs.add("__form_id", "");

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> new InputExtractor<Object>(null, inputs, Locale.ENGLISH, () -> null));
    }

    @Test
    void test_invalidKey()
    {
        // setup
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkAndConsumeAsString(null, result::add));
        assertThrows(IllegalArgumentException.class, () -> extractor.checkAndConsumeAsString("", result::add));
    }

    @Test
    void test_approve()
    {
        // setup
        inputs.add("testKey", "");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
        extractor.checkAndConsumeAsNotEmptyString("testKey", result::add);

        // verify TODO: FIX this test
        // assertThrows(InputExtractionException.class, () -> extractor.approve(HANDLER, () -> null));
    }

    @Test
    void test_checkAndConsumeAsString()
    {
        // setup
        inputs.add("testKey", "testValue");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Boolean>                 result    = new ArrayList<>();
        InputExtractor<List<Boolean>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Boolean>                 result    = new ArrayList<>();
        InputExtractor<List<Boolean>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
        extractor.checkAndConsumeAsInteger("testKey", Object::toString);

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
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        DoubleInputProcessor processor =
            DoubleInputProcessor
                .builder()
                .lowerBound(0d)
                .upperBound(2d)
                .build();

        inputs.add("testKey", "1.0");
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<TestEnum>                 result    = new ArrayList<>();
        InputExtractor<List<TestEnum>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<TestEnum>                 result    = new ArrayList<>();
        InputExtractor<List<TestEnum>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
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
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

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
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = new InputExtractor<>(null, inputs, Locale.ENGLISH, () -> result);

        // run
        extractor.checkAndConsumeAsNotEmptyTagList("testKey", result::add);

        // verify
        assertEquals(Boolean.TRUE, extractor.hasMarker());
        assertEquals(0, result.size());
    }

}
