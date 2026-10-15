/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class AbsTranslationProviderTest
{

    private static final String KEY = "test-key";

    private static AbsTranslationProvider providerWith(Locale locale, String text)
    {
        return new AbsTranslationProvider() {

            @Override
            public AbsTranslationProvider initMaps()
            {
                addTranslation(locale, KEY, text);

                return this;
            }
        }.initMaps();
    }

    @Test
    void test_getText_apostropheIsLiteral()
    {
        // setup
        AbsTranslationProvider provider = providerWith(Locale.ENGLISH, "don't use {0}");
        String                 result;

        // run
        result = provider.getText(Locale.ENGLISH, KEY, "this");

        // verify
        assertEquals("don't use this", result);
    }

    @Test
    void test_getText_apostropheWithoutValuesIsLiteral()
    {
        // setup
        AbsTranslationProvider provider = providerWith(Locale.ENGLISH, "it's done");
        String                 result;

        // run
        result = provider.getText(Locale.ENGLISH, KEY);

        // verify
        assertEquals("it's done", result);
    }

    @Test
    void test_getText_numberIsFormattedForTheRequestedLocale()
    {
        // setup
        AbsTranslationProvider provider = providerWith(Locale.GERMANY, "{0} bytes");
        String                 result;

        // run
        result = provider.getText(Locale.GERMANY, KEY, 10485760L);

        // verify
        assertEquals("10.485.760 bytes", result);
    }

    @Test
    void test_getText_numberIsFormattedIndependentOfTheDefaultLocale()
    {
        // setup
        Locale                 original = Locale.getDefault();
        AbsTranslationProvider provider = providerWith(Locale.US, "{0} bytes");
        String                 result;

        Locale.setDefault(Locale.GERMANY);

        // run
        try
        {
            result = provider.getText(Locale.US, KEY, 10485760L);
        }
        finally
        {
            Locale.setDefault(original);
        }

        // verify
        assertEquals("10,485,760 bytes", result);
    }
}
