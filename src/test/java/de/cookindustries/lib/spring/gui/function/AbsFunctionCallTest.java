/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbsFunctionCallTest
{

    private AbsFunctionCall functionCall;

    @BeforeEach
    void init()
    {
        functionCall = new AbsFunctionCall() {

            @Override
            protected String functionName()
            {
                return "test";
            }

            @Override
            protected Integer numberOfParameters()
            {
                return 1;
            }

        };
    }

    @Test
    void test_setParam_string()
    {
        // setup
        functionCall.setParam("test");

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 'test');", result, "String param not set correctly");
    }

    @Test
    void test_setParam_integer()
    {
        // setup
        functionCall.setParam(Integer.valueOf(0));

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 0);", result, "Integer param not set correctly");
    }

    @Test
    void test_setParam_boolean()
    {
        // setup
        functionCall.setParam(true);

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', true);", result, "Boolean param not set correctly");
    }

    @Test
    void test_setParam_double()
    {
        // setup
        functionCall.setParam(0.1d);

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 0.1);", result, "Double param not set correctly");
    }

    @Test
    void test_setParam_moreThanAllowed()
    {
        // setup
        functionCall.setParam("");

        // run & verify
        assertThrows(IndexOutOfBoundsException.class, () -> functionCall.setParam(""));
    }

    @Test
    void test_setParam_twoParams()
    {
        // setup
        functionCall = new AbsFunctionCall() {

            @Override
            protected String functionName()
            {
                return "test";
            }

            @Override
            protected Integer numberOfParameters()
            {
                return 2;
            }

        };

        functionCall.setParam(true);
        functionCall.setParam(0.1d);

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', true, 0.1);", result, "Parmameter not set correctly");
    }

    @Test
    void test_parseAsJS_stringWithApostrophe_isEscaped()
    {
        // setup
        functionCall.setParam("it's");

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 'it\\'s');", result);
    }

    @Test
    void test_parseAsJS_stringWithBackslash_isEscaped()
    {
        // setup
        functionCall.setParam("a\\");

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 'a\\\\');", result);
    }

    @Test
    void test_parseAsJS_stringWithLineBreaks_isEscaped()
    {
        // setup
        functionCall.setParam("a\nb\rc");

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 'a\\nb\\rc');", result);
    }

    @Test
    void test_parseAsJS_stringWithClosingScriptTag_isEscaped()
    {
        // setup
        functionCall.setParam("</script>");

        String result;

        // run
        result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', '<\\/script>');", result);
    }
}
