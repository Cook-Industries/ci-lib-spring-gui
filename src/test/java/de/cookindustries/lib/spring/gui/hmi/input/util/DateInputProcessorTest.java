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

import java.sql.Date;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class DateInputProcessorTest
{

    @Test
    void test_check_inputNull()
    {
        // setup
        DateInputProcessor     processor =
            DateInputProcessor
                .builder()
                .build();
        String                 input     = null;
        InputCheckResult<Date> result;

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
        DateInputProcessor     processor      =
            DateInputProcessor
                .builder()
                .build();
        String                 input          = "2025-02-01";
        Date                   expectedOutput = Date.valueOf("2025-02-01");
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_noFallback()
    {
        // setup
        DateInputProcessor     processor =
            DateInputProcessor
                .builder()
                .build();
        String                 input     = "";
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PARSABLE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_emptyInput_withFallback()
    {
        // setup
        DateInputProcessor     processor      =
            DateInputProcessor
                .builder()
                .fallback(Date.valueOf("2025-02-20"))
                .build();
        String                 input          = "";
        Date                   expectedOutput = Date.valueOf("2025-02-20");
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_underLowerBound()
    {
        // setup
        DateInputProcessor     processor =
            DateInputProcessor
                .builder()
                .lowerBound(Date.valueOf("2025-02-20"))
                .build();
        String                 input     = "2025-02-10";
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.OUT_OF_BOUNDS, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_inBounds_overLowerBound()
    {
        // setup
        DateInputProcessor     processor      =
            DateInputProcessor
                .builder()
                .lowerBound(Date.valueOf("2025-02-10"))
                .build();
        String                 input          = "2025-02-20";
        Date                   expectedOutput = Date.valueOf("2025-02-20");
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_underUpperBound()
    {
        // setup
        DateInputProcessor     processor      =
            DateInputProcessor
                .builder()
                .upperBound(Date.valueOf("2025-02-20"))
                .build();
        String                 input          = "2025-02-10";
        Date                   expectedOutput = Date.valueOf("2025-02-10");
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_inBounds_overUpperBound()
    {
        // setup
        DateInputProcessor     processor =
            DateInputProcessor
                .builder()
                .upperBound(Date.valueOf("2025-02-10"))
                .build();
        String                 input     = "2025-02-20";
        InputCheckResult<Date> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.OUT_OF_BOUNDS, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

}
