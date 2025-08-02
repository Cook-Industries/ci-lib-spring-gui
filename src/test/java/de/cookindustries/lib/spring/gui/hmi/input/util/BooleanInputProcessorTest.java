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

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class BooleanInputProcessorTest
{

    @Test
    void test_check_inputNull()
    {
        // setup
        BooleanInputProcessor     processor =
            BooleanInputProcessor
                .builder()
                .build();
        String                    input     = null;
        InputCheckResult<Boolean> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PRESENT, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_emptyInput_defaultFalse()
    {
        // setup
        BooleanInputProcessor     processor      =
            BooleanInputProcessor
                .builder()
                .fallback(false)
                .build();
        String                    input          = "";
        Boolean                   expectedOutput = false;
        InputCheckResult<Boolean> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_defaultTrue()
    {
        // setup
        BooleanInputProcessor     processor      =
            BooleanInputProcessor
                .builder()
                .fallback(true)
                .build();
        String                    input          = "";
        Boolean                   expectedOutput = true;
        InputCheckResult<Boolean> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_false()
    {
        // setup
        BooleanInputProcessor     processor      =
            BooleanInputProcessor
                .builder()
                .build();
        String                    input          = "false";
        Boolean                   expectedOutput = false;
        InputCheckResult<Boolean> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_true()
    {
        // setup
        BooleanInputProcessor     processor      =
            BooleanInputProcessor
                .builder()
                .build();
        String                    input          = "true";
        Boolean                   expectedOutput = true;
        InputCheckResult<Boolean> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }
}
