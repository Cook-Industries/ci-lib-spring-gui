package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class RequestModalTest
{

    @Test
    void test_constructor()
    {
        // setup
        String          requestUrl = "/test";
        List<ValuePair> params     = new ArrayList<>();
        params.add(new ValuePair("test", "test"));
        RequestModal call   = new RequestModal(requestUrl, params);

        // run
        String       result = call.parseAsJS();

        // verify
        assertEquals("requestModal('/test', {\"test\": \"test\"});", result, "String param not set correctly");
    }

}
