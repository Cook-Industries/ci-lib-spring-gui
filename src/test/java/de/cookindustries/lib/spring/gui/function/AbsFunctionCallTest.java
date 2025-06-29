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
    void test_setStringParam()
    {
        // setup
        functionCall.setStringParam("test");

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test('test');", result, "String param not set correctly");
    }

    @Test
    void test_setStringParam_notPrefixed()
    {
        // setup
        functionCall.setStringParam("test", false);

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test(test);", result, "String param not set correctly");
    }

    @Test
    void test_setIntegerParam()
    {
        // setup
        functionCall.setIntegerParam(Integer.valueOf(0));

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test(0);", result, "Integer param not set correctly");
    }

    @Test
    void test_setBooleanParam()
    {
        // setup
        functionCall.setBooleanParam(Boolean.TRUE);

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test(true);", result, "Boolean param not set correctly");
    }

    @Test
    void test_setDoubleParam()
    {
        // setup
        functionCall.setDoubleParam(Double.valueOf(0.1d));

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test(0.1);", result, "Double param not set correctly");
    }

    @Test
    void test_setMoreParamsThanAllowed()
    {
        // setup
        functionCall.setStringParam("");

        // run & verify
        assertThrows(IndexOutOfBoundsException.class, () -> functionCall.setStringParam(""));
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

        functionCall.setBooleanParam(Boolean.TRUE);
        functionCall.setDoubleParam(Double.valueOf(0.1d));

        // run
        String result = functionCall.parseAsJS();

        // verify
        assertEquals("test(true, 0.1);", result, "Parmameter not set correctly");
    }
}
