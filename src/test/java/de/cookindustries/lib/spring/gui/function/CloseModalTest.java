package de.cookindustries.lib.spring.gui.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CloseModalTest
{

    @Test
    void test_constructor()
    {
        // setup
        CloseModal call = new CloseModal();

        // run
        String result = call.parseAsJS();

        // verify
        assertEquals("closeModal();", result, "String param not set correctly");
    }

}
