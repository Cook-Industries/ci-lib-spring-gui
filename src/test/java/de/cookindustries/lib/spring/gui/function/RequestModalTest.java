/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
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
    void test_constructor()
    {
        // setup
        String       requestUrl = "/test";
        RequestModal call       = new RequestModal(requestUrl, TestObject.builder().uid(2).build());

        // run
        String       result     = call.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('requestModal', '/test', {&quot;uid&quot;:2});", result);
    }

}
