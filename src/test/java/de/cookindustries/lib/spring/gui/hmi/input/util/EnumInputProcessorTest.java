package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class EnumInputProcessorTest
{

    private enum TestEnum {
        TEST, TEST_2;
    }

    @Test
    void test_check_inputNull()
    {
        // setup
        EnumInputProcessor<TestEnum> processor =
            EnumInputProcessor
                .<TestEnum>builder()
                .enumClass(TestEnum.class)
                .build();
        String                       input     = null;
        InputCheckResult<TestEnum>   result;

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
        EnumInputProcessor<TestEnum> processor      =
            EnumInputProcessor
                .<TestEnum>builder()
                .enumClass(TestEnum.class)
                .build();
        String                       input          = "test";
        TestEnum                     expectedOutput = TestEnum.TEST;
        InputCheckResult<TestEnum>   result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emmptyInput_noFallback()
    {
        // setup
        EnumInputProcessor<TestEnum> processor =
            EnumInputProcessor
                .<TestEnum>builder()
                .enumClass(TestEnum.class)
                .fallback(null)
                .build();
        String                       input     = "";
        InputCheckResult<TestEnum>   result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PARSABLE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_emmptyInput_withFallback()
    {
        // setup
        EnumInputProcessor<TestEnum> processor      =
            EnumInputProcessor
                .<TestEnum>builder()
                .enumClass(TestEnum.class)
                .fallback(TestEnum.TEST_2)
                .build();
        String                       input          = "";
        TestEnum                     expectedOutput = TestEnum.TEST_2;
        InputCheckResult<TestEnum>   result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

}
