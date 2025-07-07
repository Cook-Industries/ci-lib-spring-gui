package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbsFunctionCallTest
{

    private AbsFunctionCall functionCall;

    @BeforeEach
    public void init()
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

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 'test');", result, "String param not set correctly");
    }

    @Test
    void test_setParam_integer()
    {
        // setup
        functionCall.setParam(Integer.valueOf(0));

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 0);", result, "Integer param not set correctly");
    }

    @Test
    void test_setParam_boolean()
    {
        // setup
        functionCall.setParam(true);

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', true);", result, "Boolean param not set correctly");
    }

    @Test
    void test_setParam_double()
    {
        // setup
        functionCall.setParam(0.1d);

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', 0.1);", result, "Double param not set correctly");
    }

    @Test
    void test_setMoreParamsThanAllowed()
    {
        // setup
        functionCall.setParam("");

        // run & verify
        assertThrows(IndexOutOfBoundsException.class, () -> functionCall.setParam(""));
    }

    @Test
    void test_set2Params()
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

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('test', true, 0.1);", result, "Parmameter not set correctly");
    }
}
