package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class IntegerInputProcessorTest
{

    @Test
    void test_check_inputNull()
    {
        // setup
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .build();
        String                    input     = null;
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor      =
            IntegerInputProcessor
                .builder()
                .build();
        String                    input          = "1";
        Integer                   expectedOutput = 1;
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .build();
        String                    input     = "";
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor      =
            IntegerInputProcessor
                .builder()
                .fallback(1)
                .build();
        String                    input          = "";
        Integer                   expectedOutput = 1;
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .lowerBound(2)
                .build();
        String                    input     = "1";
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor      =
            IntegerInputProcessor
                .builder()
                .lowerBound(0)
                .build();
        String                    input          = "1";
        Integer                   expectedOutput = 1;
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor      =
            IntegerInputProcessor
                .builder()
                .upperBound(2)
                .build();
        String                    input          = "1";
        Integer                   expectedOutput = 1;
        InputCheckResult<Integer> result;

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
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .upperBound(0)
                .build();
        String                    input     = "1";
        InputCheckResult<Integer> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.OUT_OF_BOUNDS, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_blacklist()
    {
        // setup
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .reject(1)
                .build();
        String                    input     = "1";
        InputCheckResult<Integer> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.REJECTED_VALUE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_whitelist_match()
    {
        // setup
        IntegerInputProcessor     processor      =
            IntegerInputProcessor
                .builder()
                .accept(1)
                .build();
        String                    input          = "1";
        Integer                   expectedOutput = 1;
        InputCheckResult<Integer> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_whitelist_noMatch()
    {
        // setup
        IntegerInputProcessor     processor =
            IntegerInputProcessor
                .builder()
                .accept(1)
                .build();
        String                    input     = "2";
        InputCheckResult<Integer> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_ACCEPTED_VALUE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }
}
