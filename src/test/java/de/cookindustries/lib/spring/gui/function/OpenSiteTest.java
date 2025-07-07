package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OpenSiteTest
{

    private class TestObject extends AbsFunctionArgs
    {

        private final int uid = 2;

        @SuppressWarnings("unused")
        public int getUid()
        {
            return uid;
        }
    }

    @Test
    void test_constructor()
    {
        // setup
        String           requestUrl = "/test";
        OpenSite<String> call       = new OpenSite<>(requestUrl, new TestObject());

        // run
        String           result     = call.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('openSite', '/test', {\"uid\":2});", result);
    }

}
