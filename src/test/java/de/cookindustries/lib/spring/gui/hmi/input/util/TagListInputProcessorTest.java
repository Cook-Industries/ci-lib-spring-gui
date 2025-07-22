package de.cookindustries.lib.spring.gui.hmi.input.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;

class TagListInputProcessorTest
{

    @Test
    void test_check_inputNull()
    {
        // setup
        TagListInputProcessor     processor =
            TagListInputProcessor
                .builder()
                .build();
        String                    input     = null;
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PRESENT, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_inputNonJson()
    {
        // setup
        TagListInputProcessor     processor =
            TagListInputProcessor
                .builder()
                .build();
        String                    input     = "...";
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.NOT_PARSABLE, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_simpleValue()
    {
        // setup
        TagListInputProcessor processor      =
            TagListInputProcessor
                .builder()
                .build();
        String                input          = "[{\"value\":\"test\"}]";
        TagList               expectedOutput = new TagList();
        expectedOutput.add(new Tag("test"));
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_notAllowed()
    {
        // setup
        TagListInputProcessor     processor =
            TagListInputProcessor
                .builder()
                .allowEmpty(false)
                .build();
        String                    input     = "";
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.EMPTY_BUT_EXPECTED, result.getType());
        assertThrows(ValueNotPresentException.class, () -> result.getResult());
    }

    @Test
    void test_check_emptyInput_emptyString()
    {
        // setup
        TagListInputProcessor     processor      =
            TagListInputProcessor
                .builder()
                .allowEmpty(true)
                .build();
        String                    input          = "";
        TagList                   expectedOutput = new TagList();
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_emptyJson()
    {
        // setup
        TagListInputProcessor     processor      =
            TagListInputProcessor
                .builder()
                .allowEmpty(true)
                .build();
        String                    input          = "[]";
        TagList                   expectedOutput = new TagList();
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_allowed_noFallback()
    {
        // setup
        TagListInputProcessor     processor      =
            TagListInputProcessor
                .builder()
                .allowEmpty(true)
                .fallback(null)
                .build();
        String                    input          = "";
        TagList                   expectedOutput = new TagList();
        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_emptyInput_allowed_withFallback()
    {
        // setup
        TagList fallback = new TagList();
        fallback.add(new Tag("test"));

        TagListInputProcessor processor      =
            TagListInputProcessor
                .builder()
                .allowEmpty(true)
                .fallback(fallback)
                .build();
        String                input          = "";

        TagList               expectedOutput = new TagList();
        expectedOutput.add(new Tag("test"));

        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

    @Test
    void test_check_whitelist()
    {
        // setup
        TagListInputProcessor processor      =
            TagListInputProcessor
                .builder()
                .allowEmpty(true)
                .whitelist(new Tag("test"))
                .build();

        String                input          = "[{\"value\":\"test\"},{\"value\":\"test_2\"}]";

        TagList               expectedOutput = new TagList();
        expectedOutput.add(new Tag("test"));

        InputCheckResult<TagList> result;

        // run
        result = processor.process(input);

        // verify
        assertEquals(InputCheckResultType.PASS, result.getType());
        assertEquals(expectedOutput, result.getResult());
    }

}
