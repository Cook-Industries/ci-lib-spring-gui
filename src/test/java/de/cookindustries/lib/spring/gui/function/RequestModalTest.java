/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RequestModalTest
{

    @Test
    void test_parseAsJS_requestModal()
    {
        // setup
        String       requestUrl = "/test";
        RequestModal call       = new RequestModal(requestUrl, TestObject.builder().uid(2).build());
        String       result;

        // run
        result = call.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('requestModal', '/test', {\"uid\":2});", result);
    }

}
