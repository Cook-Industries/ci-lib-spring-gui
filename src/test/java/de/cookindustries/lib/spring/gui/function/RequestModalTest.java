package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RequestModalTest
{

    private class TestObject extends AbsFunctionArgs
    {

        private final String name = "test";

        @SuppressWarnings("unused")
        public String getName()
        {
            return name;
        }
    }

    @Test
    void test_constructor()
    {
        // setup
        String       requestUrl = "/test";
        RequestModal call       = new RequestModal(requestUrl, new TestObject());

        // run
        String       result     = call.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('requestModal', '/test', {\"name\":\"test\"});", result);
    }

}
