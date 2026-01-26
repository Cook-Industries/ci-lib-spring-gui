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

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class StringInputProcessorTest
{

    @Test
    void test_check_inputNull()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .build();
        String                   input     = null;
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PRESENT, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_simpleValue()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .build();
        String                   input          = "test";
        String                   expectedOutput = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_isEmpty_allowEmtpy()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .build();
        String                   input          = "";
        String                   expectedOutput = "";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_isEmpty_denyEmtpy_noFallback()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .allowEmpty(false)
                .fallback(null)
                .build();
        String                   input     = "";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.EMPTY_BUT_EXPECTED, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_isEmpty_denyEmtpy_withFallback()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .allowEmpty(false)
                .fallback("fallback")
                .build();
        String                   input          = "";
        String                   expectedOutput = "fallback";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_pattern_match()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("[t]{3}"))
                .build();
        String                   input          = "ttt";
        String                   expectedOutput = "ttt";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_pattern_noMatch()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .pattern(Pattern.compile("[t]{3}"))
                .build();
        String                   input     = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NO_PATTERN_MATCH, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_blacklist_match_caseInsensitive()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .reject("TEST")
                .caseSensitiveCheck(false)
                .build();
        String                   input     = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.REJECTED_VALUE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_blacklist_match_caseSensitive()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .reject("test")
                .caseSensitiveCheck(true)
                .build();
        String                   input     = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.REJECTED_VALUE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_whitlist_match_caseInsensitive()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .accept("TEST")
                .caseSensitiveCheck(false)
                .build();
        String                   input          = "test";
        String                   expectedOutput = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_whitlist_match_caseSensitive()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .accept("test")
                .caseSensitiveCheck(true)
                .build();
        String                   input          = "test";
        String                   expectedOutput = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_whitlist_noMatch()
    {
        // setup
        StringInputProcessor     processor =
            StringInputProcessor
                .builder()
                .accept("plop")
                .build();
        String                   input     = "test";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_ACCEPTED_VALUE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_prepare_sanitization()
    {
        // setup
        StringInputProcessor     processor      =
            StringInputProcessor
                .builder()
                .sanitazation(s -> s.replace("t", "p"))
                .build();
        String                   input          = "test";
        String                   expectedOutput = "pesp";
        InputCheckResult<String> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }
}
