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

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.NullIgnoredException;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class DoubleInputProcessorTest
{

    @Test
    void test_check_inputNull() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor =
            DoubleInputProcessor
                .builder()
                .build();
        String                   input     = null;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PRESENT, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_simpleValue() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor      =
            DoubleInputProcessor
                .builder()
                .build();
        String                   input          = "1.0";
        Double                   expectedOutput = 1d;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_noFallback() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor =
            DoubleInputProcessor
                .builder()
                .build();
        String                   input     = "";
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PARSABLE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_emptyInput_withFallback() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor      =
            DoubleInputProcessor
                .builder()
                .fallback(1d)
                .build();
        String                   input          = "";
        Double                   expectedOutput = 1d;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_decimalPlaces() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor      =
            DoubleInputProcessor
                .builder()
                .decimalPlaces(5)
                .build();
        String                   input          = "1.000005";
        Double                   expectedOutput = 1.00001d;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_underLowerBound() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor =
            DoubleInputProcessor
                .builder()
                .lowerBound(2d)
                .build();
        String                   input     = "1";
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.OUT_OF_BOUNDS, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_inBounds_overLowerBound() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor      =
            DoubleInputProcessor
                .builder()
                .lowerBound(1d)
                .build();
        String                   input          = "2";
        Double                   expectedOutput = 2d;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_underUpperBound() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor      =
            DoubleInputProcessor
                .builder()
                .upperBound(2d)
                .build();
        String                   input          = "1";
        Double                   expectedOutput = 1d;
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_overUpperBound() throws NullIgnoredException
    {
        // setup
        DoubleInputProcessor     processor =
            DoubleInputProcessor
                .builder()
                .upperBound(1d)
                .build();
        String                   input     = "2";
        InputCheckResult<Double> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.OUT_OF_BOUNDS, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }
}
