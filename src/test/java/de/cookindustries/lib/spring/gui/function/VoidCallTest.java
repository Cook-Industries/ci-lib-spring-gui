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

class VoidCallTest
{

    @Test
    void test_constructor()
    {
        // setup
        VoidCall call = new VoidCall();

        // run
        String result = call.parseAsJS();

        // verify
        assertEquals("CILIB.FunctionRegistry.call('noop');", result);
    }

}
