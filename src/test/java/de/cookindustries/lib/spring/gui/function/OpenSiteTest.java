package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class OpenSiteTest
{

    @Test
    void test_constructor_withoutUid()
    {
        // setup
        String          requestUrl = "/test";
        List<ValuePair> params     = new ArrayList<>();
        params.add(new ValuePair("test", "test"));
        OpenSite<String> call   = new OpenSite<>(requestUrl, params);

        // run
        String           result = call.parseAsJS();

        // verify
        assertEquals("openSite('/test', {\"test\": \"test\"});", result, "String param not set correctly");
    }

    @Test
    void test_constructor_withUid()
    {
        // setup
        String          requestUrl = "/test";
        List<ValuePair> params     = new ArrayList<>();
        params.add(new ValuePair("test", "test"));
        OpenSite<String> call   = new OpenSite<>(requestUrl, "aaa", params);

        // run
        String           result = call.parseAsJS();

        // verify
        assertEquals("openSite('/test', {\"__uid\": \"aaa\", \"test\": \"test\"});", result, "String param not set correctly");
    }

}
