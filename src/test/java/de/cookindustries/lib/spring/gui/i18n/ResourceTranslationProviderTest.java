/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class ResourceTranslationProviderTest
{

    @Test
    void test_initMaps()
    {
        // setup
        Locale                      locale                      = new Locale("de", "DE");
        ResourceTranslationProvider resourceTranslationProvider =
            new ResourceTranslationProvider(
                List.of("json-test-files/translations/de_DE.json",
                    "json-test-files/translations/de_DE-faulty-file.json",
                    "json-test-files/translations/de_DE-faulty-text.json"));

        // run
        resourceTranslationProvider.initMaps();

        // verify
        assertEquals("text", resourceTranslationProvider.getText(locale, "test"));
        assertEquals("I18N [void] not set.", resourceTranslationProvider.getText(locale, "void"));
    }

}
