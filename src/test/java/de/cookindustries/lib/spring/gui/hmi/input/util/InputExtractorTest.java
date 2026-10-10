/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.Fixture;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.response.message.ActivateMarkerMessage;

class InputExtractorTest
{

    private enum TestEnum
    {
        VALUE1, VALUE2, READ_ONLY;
    }

    private MultiValueMap<String, String> inputs;

    @BeforeEach
    void init()
    {
        inputs = new LinkedMultiValueMap<>();
        inputs.add("__form_id", "testForm");
    }

    private <R> InputExtractor<R> extractor(Supplier<R> result)
    {
        return new InputExtractor<>(null, inputs, Locale.ENGLISH, result);
    }

    private InputExtractor<Object> extractor()
    {
        return extractor(() -> null);
    }

    private static InputCheckMarker singleMarker(InputExtractor<?> extractor)
    {
        assertEquals(1, extractor.getMarker().size());

        return extractor.getMarker().get(0);
    }

    private static void assertMarker(InputCheckMarker marker, String key, InputCheckResultType resultType, MarkerType markerType,
        Object... placeholders)
    {
        assertEquals("testForm", marker.getFormId());
        assertEquals(key, marker.getTransferId());
        assertEquals(MarkerCategory.ERROR, marker.getCategory());
        assertEquals(markerType, marker.getType());
        assertEquals(resultType.getTranslationKey(), marker.getTranslationKey());
        assertArrayEquals(placeholders, marker.getTranslationPlaceholders());
    }

    @Test
    void test_constructor_missingFormId_isRejected()
    {
        // setup
        inputs.clear();

        // run & verify
        assertThrows(IllegalArgumentException.class, this::extractor);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void test_constructor_blankFormId_isRejected(String formId)
    {
        // setup
        inputs.clear();
        inputs.add("__form_id", formId);

        // run & verify
        assertThrows(IllegalArgumentException.class, this::extractor);
    }

    @ParameterizedTest
    @CsvSource({"-1, 0", "0, -1", "-1, -1"})
    void test_constructor_negativeLimits_areRejected(long maxFile, long maxBatch)
    {
        // setup
        Supplier<Object> supplier = () -> null;

        // run & verify
        assertThrows(IllegalArgumentException.class,
            () -> new InputExtractor<Object>(null, inputs, Locale.ENGLISH, null, maxFile, maxBatch, supplier));
    }

    @Test
    void test_getLocale_returnsTheGivenLocale()
    {
        // setup
        InputExtractor<Object> extractor = new InputExtractor<>(null, inputs, Locale.GERMAN, () -> null);
        Locale                 result;

        // run
        result = extractor.getLocale();

        // verify
        assertSame(Locale.GERMAN, result);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void test_check_invalidKey_isRejectedByEveryCheck(String key)
    {
        // setup
        InputExtractor<Object> extractor = extractor();
        List<Executable>       calls     =
            List
                .of(
                    () -> extractor.checkAndConsumeAsString(key, v -> {
                                                     }),
                    () -> extractor.checkAndConsumeAsNotEmptyString(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsBooleanDefaultFalse(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsBooleanDefaultTrue(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsInteger(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsDouble(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsDate(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsEnum(key, TestEnum.class, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsTagList(key, v -> {
                    }),
                    () -> extractor.checkAndConsumeAsNotEmptyTagList(key, v -> {
                    }),
                    () -> extractor.checkFiles(key, true, true));

        // run & verify
        calls.forEach(call -> assertThrows(IllegalArgumentException.class, call));
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_check_returnsTheExtractorForChaining()
    {
        // setup
        inputs.add("a", "1");
        InputExtractor<Object> extractor = extractor();
        InputExtractor<Object> resultString;
        InputExtractor<Object> resultInteger;
        InputExtractor<Object> resultDouble;

        // run
        resultString = extractor.checkAndConsumeAsString("a", v -> {
        });
        resultInteger = extractor.checkAndConsumeAsInteger("a", v -> {
        });
        resultDouble = extractor.checkAndConsumeAsDouble("a", v -> {
        });

        // verify
        assertSame(extractor, resultString);
        assertSame(extractor, resultInteger);
        assertSame(extractor, resultDouble);
    }

    @Test
    void test_markers_accumulateInOrder_andAreReadOnly()
    {
        // setup
        inputs.add("first", "abc");
        inputs.add("second", "");
        InputExtractor<Object> extractor = extractor();

        // run
        extractor
            .checkAndConsumeAsInteger("first", v -> {
            })
            .checkAndConsumeAsNotEmptyString("second", v -> {
            })
            .checkAndConsumeAsString("missing", v -> {
            });

        // verify
        assertTrue(extractor.hasMarker());
        assertEquals(3, extractor.getMarker().size());
        assertEquals("first", extractor.getMarker().get(0).getTransferId());
        assertEquals("second", extractor.getMarker().get(1).getTransferId());
        assertEquals("missing", extractor.getMarker().get(2).getTransferId());
        assertThrows(UnsupportedOperationException.class, () -> extractor.getMarker().clear());
    }

    @Test
    void test_passingChecks_raiseNoMarker()
    {
        // setup
        inputs.add("name", "john");
        inputs.add("age", "30");
        InputExtractor<Object> extractor = extractor();

        // run
        extractor
            .checkAndConsumeAsNotEmptyString("name", v -> {
            })
            .checkAndConsumeAsInteger("age", v -> {
            });

        // verify
        assertFalse(extractor.hasMarker());
        assertTrue(extractor.getMarker().isEmpty());
    }

    @Test
    void test_firstValueOfAMultiValueKeyIsUsed()
    {
        // setup
        inputs.add("key", "first");
        inputs.add("key", "second");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", result::add);

        // verify
        assertEquals(List.of("first"), result);
    }

    @Test
    void test_checkAndConsumeAsString_passesValue()
    {
        // setup
        inputs.add("key", "testValue");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of("testValue"), result);
    }

    @Test
    void test_checkAndConsumeAsString_emptyValue_isAllowedByDefault()
    {
        // setup
        inputs.add("key", "");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(""), result);
    }

    @Test
    void test_checkAndConsumeAsString_notEmpty_rejectsEmptyValue()
    {
        // setup
        inputs.add("key", "");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsNotEmptyString("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.EMPTY_BUT_EXPECTED, MarkerType.INVALID);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsString_missingKey_raisesNotPresent()
    {
        // setup
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PRESENT, MarkerType.NO_VALUE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsString_missingKey_isIgnoredIfTheProcessorSaysSo()
    {
        // setup
        StringInputProcessor         processor =
            StringInputProcessor
                .builder()
                .ignoreNull(true)
                .build();
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsString_patternMatch_passes()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("[a-z]{3}"))
                .build();
        inputs.add("key", "abc");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of("abc"), result);
    }

    @Test
    void test_checkAndConsumeAsString_patternMismatch_raisesMarker()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("[a-z]{3}"))
                .build();
        inputs.add("key", "abcd");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NO_PATTERN_MATCH, MarkerType.INVALID);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsString_acceptsAndRejects()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .accepts(Set.of("red", "green", "blue"))
                .rejects(Set.of("green"))
                .build();
        inputs.add("ok", "red");
        inputs.add("rejected", "green");
        inputs.add("unknown", "black");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("ok", processor, result::add);
        extractor.checkAndConsumeAsString("rejected", processor, result::add);
        extractor.checkAndConsumeAsString("unknown", processor, result::add);

        // verify
        assertEquals(List.of("red"), result);
        assertEquals(2, extractor.getMarker().size());
        assertMarker(extractor.getMarker().get(0), "rejected", InputCheckResultType.REJECTED_VALUE, MarkerType.INVALID);
        assertMarker(extractor.getMarker().get(1), "unknown", InputCheckResultType.NOT_ACCEPTED_VALUE, MarkerType.INVALID);
    }

    @Test
    void test_checkAndConsumeAsString_caseInsensitiveCheck()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .accepts(Set.of("red"))
                .caseSensitiveCheck(false)
                .build();
        inputs.add("key", "RED");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of("RED"), result);
    }

    @Test
    void test_checkAndConsumeAsString_fallbackReplacesEmptyValue()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .allowEmpty(false)
                .fallback("none")
                .build();
        inputs.add("key", "");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of("none"), result);
    }

    @Test
    void test_checkAndConsumeAsString_sanitazationIsApplied()
    {
        // setup
        StringInputProcessor processor =
            StringInputProcessor
                .builder()
                .sanitazation(String::trim)
                .build();
        inputs.add("key", "  padded  ");
        List<String>                 result    = new ArrayList<>();
        InputExtractor<List<String>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsString("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of("padded"), result);
    }

    @ParameterizedTest
    @CsvSource({"true, true", "TRUE, true", "True, true", "false, false", "FALSE, false", "yes, false", "1, false"})
    void test_checkAndConsumeAsBoolean_isParsedLeniently(String raw, boolean expected)
    {
        // setup
        inputs.add("key", raw);
        List<Boolean>                 result    = new ArrayList<>();
        InputExtractor<List<Boolean>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsBooleanDefaultFalse("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(expected), result);
    }

    @Test
    void test_checkAndConsumeAsBoolean_emptyValue_usesTheDefaultOfTheMethod()
    {
        // setup
        inputs.add("falseKey", "");
        inputs.add("trueKey", "");
        List<Boolean>                 result    = new ArrayList<>();
        InputExtractor<List<Boolean>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsBooleanDefaultFalse("falseKey", result::add);
        extractor.checkAndConsumeAsBooleanDefaultTrue("trueKey", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(false, true), result);
    }

    @Test
    void test_checkAndConsumeAsInteger_passesValue()
    {
        // setup
        inputs.add("key", "42");
        inputs.add("negative", "-7");
        inputs.add("signed", "+5");
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("key", result::add);
        extractor.checkAndConsumeAsInteger("negative", result::add);
        extractor.checkAndConsumeAsInteger("signed", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(42, -7, 5), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1.5", "1,5", "2147483648", " 1", "1 ", ""})
    void test_checkAndConsumeAsInteger_notParsable_raisesMarker(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PARSABLE, MarkerType.NOT_PARSABLE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsInteger_emptyValue_usesFallback()
    {
        // setup
        IntegerInputProcessor processor =
            IntegerInputProcessor
                .builder()
                .fallback(7)
                .build();
        inputs.add("key", "");
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(7), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "10", "5"})
    void test_checkAndConsumeAsInteger_bounds_inclusive(String raw)
    {
        // setup
        IntegerInputProcessor processor =
            IntegerInputProcessor
                .builder()
                .lowerBound(0)
                .upperBound(10)
                .build();
        inputs.add("key", raw);
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(Integer.parseInt(raw)), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "11"})
    void test_checkAndConsumeAsInteger_bounds_outOfRange(String raw)
    {
        // setup
        IntegerInputProcessor processor =
            IntegerInputProcessor
                .builder()
                .lowerBound(0)
                .upperBound(10)
                .build();
        inputs.add("key", raw);
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("key", processor, result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.OUT_OF_BOUNDS, MarkerType.OUT_OF_RANGE, 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsInteger_acceptsAndRejects()
    {
        // setup
        IntegerInputProcessor processor =
            IntegerInputProcessor
                .builder()
                .accepts(Set.of(1, 2, 3))
                .rejects(Set.of(2))
                .build();
        inputs.add("ok", "1");
        inputs.add("rejected", "2");
        inputs.add("unknown", "9");
        List<Integer>                 result    = new ArrayList<>();
        InputExtractor<List<Integer>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsInteger("ok", processor, result::add);
        extractor.checkAndConsumeAsInteger("rejected", processor, result::add);
        extractor.checkAndConsumeAsInteger("unknown", processor, result::add);

        // verify
        assertEquals(List.of(1), result);
        assertMarker(extractor.getMarker().get(0), "rejected", InputCheckResultType.REJECTED_VALUE, MarkerType.INVALID);
        assertMarker(extractor.getMarker().get(1), "unknown", InputCheckResultType.NOT_ACCEPTED_VALUE, MarkerType.INVALID);
    }

    @Test
    void test_checkAndConsumeAsDouble_passesValue()
    {
        // setup
        inputs.add("key", "1.5");
        inputs.add("negative", "-0.25");
        inputs.add("exp", "1e3");
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDouble("key", result::add);
        extractor.checkAndConsumeAsDouble("negative", result::add);
        extractor.checkAndConsumeAsDouble("exp", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(1.5, -0.25, 1000.0), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1,5", "1.5.2", ""})
    void test_checkAndConsumeAsDouble_notParsable_raisesMarker(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDouble("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PARSABLE, MarkerType.NOT_PARSABLE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsDouble_roundsToDecimalPlaces()
    {
        // setup
        DoubleInputProcessor processor =
            DoubleInputProcessor
                .builder()
                .decimalPlaces(2)
                .build();
        inputs.add("key", "2.345");
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDouble("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(2.35), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "10.0", "5.5"})
    void test_checkAndConsumeAsDouble_bounds_inclusive(String raw)
    {
        // setup
        DoubleInputProcessor processor =
            DoubleInputProcessor
                .builder()
                .lowerBound(0.0)
                .upperBound(10.0)
                .build();
        inputs.add("key", raw);
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDouble("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(Double.parseDouble(raw)), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.1", "10.1"})
    void test_checkAndConsumeAsDouble_bounds_outOfRange(String raw)
    {
        // setup
        DoubleInputProcessor processor =
            DoubleInputProcessor
                .builder()
                .lowerBound(0.0)
                .upperBound(10.0)
                .build();
        inputs.add("key", raw);
        List<Double>                 result    = new ArrayList<>();
        InputExtractor<List<Double>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDouble("key", processor, result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.OUT_OF_BOUNDS, MarkerType.OUT_OF_RANGE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsDate_passesValue()
    {
        // setup
        inputs.add("key", "2024-02-29");
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDate("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(Date.valueOf("2024-02-29")), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"29.02.2024", "2024/02/29", "abc", ""})
    void test_checkAndConsumeAsDate_notParsable_raisesMarker(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDate("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PARSABLE, MarkerType.NOT_PARSABLE);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-01-01", "2024-12-31", "2024-06-15"})
    void test_checkAndConsumeAsDate_bounds_inclusive(String raw)
    {
        // setup
        DateInputProcessor processor =
            DateInputProcessor
                .builder()
                .lowerBound(Date.valueOf("2024-01-01"))
                .upperBound(Date.valueOf("2024-12-31"))
                .build();
        inputs.add("key", raw);
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDate("key", processor, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(Date.valueOf(raw)), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-12-31", "2025-01-01"})
    void test_checkAndConsumeAsDate_bounds_outOfRange(String raw)
    {
        // setup
        DateInputProcessor processor =
            DateInputProcessor
                .builder()
                .lowerBound(Date.valueOf("2024-01-01"))
                .upperBound(Date.valueOf("2024-12-31"))
                .build();
        inputs.add("key", raw);
        List<Date>                 result    = new ArrayList<>();
        InputExtractor<List<Date>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsDate("key", processor, result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.OUT_OF_BOUNDS, MarkerType.OUT_OF_RANGE);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"value1, VALUE1", "VALUE2, VALUE2", "Value1, VALUE1", "'read only', READ_ONLY", "READ_ONLY, READ_ONLY"})
    void test_checkAndConsumeAsEnum_isMatchedByNameIgnoringCaseAndSpaces(String raw, TestEnum expected)
    {
        // setup
        inputs.add("key", raw);
        List<TestEnum>                 result    = new ArrayList<>();
        InputExtractor<List<TestEnum>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsEnum("key", TestEnum.class, result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(List.of(expected), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"value3", "", "value 1"})
    void test_checkAndConsumeAsEnum_unknownName_raisesMarker(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<TestEnum>                 result    = new ArrayList<>();
        InputExtractor<List<TestEnum>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsEnum("key", TestEnum.class, result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PARSABLE, MarkerType.NOT_PARSABLE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_checkAndConsumeAsEnum_missingKey_raisesNotPresent()
    {
        // setup
        InputExtractor<Object> extractor = extractor();

        // run
        extractor.checkAndConsumeAsEnum("key", TestEnum.class, v -> {
        });

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PRESENT, MarkerType.NO_VALUE);
    }

    @Test
    void test_checkAndConsumeAsTagList_passesTags()
    {
        // setup
        inputs.add("key", "[{\"value\":\"a\"},{\"value\":\"b\"}]");
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsTagList("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(1, result.size());
        assertEquals(List.of(Tag.builder().value("a").build(), Tag.builder().value("b").build()), result.get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[]", ""})
    void test_checkAndConsumeAsTagList_noTags_areAllowedByDefault(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsTagList("key", result::add);

        // verify
        assertFalse(extractor.hasMarker());
        assertEquals(1, result.size());
        assertTrue(result.get(0).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"[]", ""})
    void test_checkAndConsumeAsTagList_notEmpty_rejectsNoTags(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsNotEmptyTagList("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.EMPTY_BUT_EXPECTED, MarkerType.INVALID);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"not json", "{\"value\":\"a\"}", "[{\"wrong\":\"a\"}]", "[1,2]"})
    void test_checkAndConsumeAsTagList_malformedJson_raisesMarker(String raw)
    {
        // setup
        inputs.add("key", raw);
        List<TagList>                 result    = new ArrayList<>();
        InputExtractor<List<TagList>> extractor = extractor(() -> result);

        // run
        extractor.checkAndConsumeAsTagList("key", result::add);

        // verify
        assertMarker(singleMarker(extractor), "key", InputCheckResultType.NOT_PARSABLE, MarkerType.NOT_PARSABLE);
        assertTrue(result.isEmpty());
    }

    @Test
    void test_approve_withoutMarker_runsHandlerAndReturnsTheCreatedResponse()
    {
        // setup
        Fixture                      fx        = new Fixture();
        List<String>                 result    = new ArrayList<>();
        List<List<String>>           handled   = new ArrayList<>();
        Response                     expected  = NotificationResponse.builder().build();
        InputExtractor<List<String>> extractor = fx.factory.create(inputs, Locale.ENGLISH, () -> result);
        Response                     actual;

        inputs.add("name", "john");
        extractor.checkAndConsumeAsNotEmptyString("name", result::add);

        // run
        actual = extractor.approve(handled::add, () -> expected);

        // verify
        assertSame(expected, actual);
        assertEquals(List.of(List.of("john")), handled);
    }

    @Test
    void test_approve_withMarker_returnsMarkerNotificationAndSkipsHandlerAndResponseCreator()
    {
        // setup
        Fixture                fx            = new Fixture();
        AtomicBoolean          handlerCalled = new AtomicBoolean(false);
        AtomicBoolean          createrCalled = new AtomicBoolean(false);
        InputExtractor<Object> extractor     = fx.factory.create(inputs, Locale.ENGLISH, () -> null);
        Response               response;

        inputs.add("count", "abc");
        extractor.checkAndConsumeAsInteger("count", v -> {
        });

        // run
        response = extractor.approve(v -> handlerCalled.set(true), () -> {
            createrCalled.set(true);

            return null;
        });

        // verify
        ActivateMarkerMessage message;

        assertInstanceOf(NotificationResponse.class, response);
        assertEquals(1, response.getMessages().size());

        message = assertInstanceOf(ActivateMarkerMessage.class, response.getMessages().get(0));

        assertEquals("count", message.getFieldId());
        assertFalse(handlerCalled.get());
        assertFalse(createrCalled.get());
    }

    @Test
    void test_approve_withAnnouncedFiles_isRefused()
    {
        // setup
        Fixture                fx        = new Fixture();
        InputExtractor<Object> extractor = fx.factory.create(inputs, Locale.ENGLISH, () -> null);

        inputs.add("files", "[{\"name\":\"a.txt\",\"size\":10}]");
        extractor.checkFiles("files", false, false);

        // run & verify
        assertFalse(extractor.hasMarker());
        assertThrows(IllegalStateException.class, () -> extractor.approve(v -> {
        }, () -> null));
    }

    @Test
    void test_approve_rejectsNullArguments()
    {
        // setup
        Fixture                fx        = new Fixture();
        InputExtractor<Object> extractor = fx.factory.create(inputs, Locale.ENGLISH, () -> null);

        // run & verify
        assertThrows(NullPointerException.class, () -> extractor.approve((Consumer<Object>) null, () -> null));
        assertThrows(NullPointerException.class, () -> extractor.approve(v -> {
        }, (Supplier<Response>) null));
    }
}
