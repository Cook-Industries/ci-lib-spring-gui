/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class UiTranslationResolverTest
{

    private static final Locale GERMANY = new Locale("de", "DE");

    private static AbsTranslationProvider provider()
    {
        return new AbsTranslationProvider() {

            @Override
            public AbsTranslationProvider initMaps()
            {
                return this;
            }
        };
    }

    @Test
    void test_resolve_exposesOnlyClientScopedTexts()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        UiTranslations         result;

        provider.addTranslation(Locale.ENGLISH, "server-key", "server");
        provider.addTranslation(Locale.ENGLISH, "client-key", "client", TranslationScope.CLIENT);

        // run
        result = resolver.resolve(Locale.ENGLISH);

        // verify
        assertEquals("client", result.texts().get("client-key"));
        assertFalse(result.texts().containsKey("server-key"));
    }

    @Test
    void test_resolve_doesNotContainLibraryDefaults()
    {
        // setup
        UiTranslationResolver resolver = new UiTranslationResolver(provider());
        UiTranslations        result;

        // run
        result = resolver.resolve(Locale.ENGLISH);

        // verify
        assertFalse(result.texts().containsKey(BasicText.UI_F5_BLOCKED.getKey()));
    }

    @Test
    void test_resolve_providerDefinesLibraryKey()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        UiTranslations         result;

        provider.addTranslation(Locale.ENGLISH, BasicText.UI_F5_BLOCKED.getKey(), "custom", TranslationScope.CLIENT);

        // run
        result = resolver.resolve(Locale.ENGLISH);

        // verify
        assertEquals("custom", result.texts().get(BasicText.UI_F5_BLOCKED.getKey()));
    }

    @Test
    void test_resolve_matchesLanguageOfUnknownCountry()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        UiTranslations         result;

        provider.addTranslation(Locale.ENGLISH, "client-key", "client", TranslationScope.CLIENT);
        provider.addTranslation(GERMANY, "client-key", "kunde", TranslationScope.CLIENT);

        // run
        result = resolver.resolve(new Locale("de", "AT"));

        // verify
        assertEquals("kunde", result.texts().get("client-key"));
    }

    @Test
    void test_resolve_unknownLanguageFallsBackToEnglish()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        UiTranslations         result;

        provider.addTranslation(Locale.ENGLISH, "client-key", "client", TranslationScope.CLIENT);
        provider.addTranslation(GERMANY, "client-key", "kunde", TranslationScope.CLIENT);

        // run
        result = resolver.resolve(Locale.FRENCH);

        // verify
        assertEquals("client", result.texts().get("client-key"));
    }

    @Test
    void test_getChecksum_isStableForSameTexts()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        String                 first;
        String                 second;

        provider.addTranslation(Locale.ENGLISH, "client-key", "client", TranslationScope.CLIENT);

        // run
        first = resolver.getChecksum(Locale.ENGLISH);
        second = resolver.getChecksum(Locale.ENGLISH);

        // verify
        assertEquals(first, second);
        assertEquals(64, first.length());
    }

    @Test
    void test_getChecksum_changesWithText()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        String                 before;
        String                 after;

        provider.addTranslation(Locale.ENGLISH, "client-key", "client", TranslationScope.CLIENT);
        before = resolver.getChecksum(Locale.ENGLISH);

        provider.addTranslation(Locale.ENGLISH, "client-key", "changed", TranslationScope.CLIENT);

        // run
        after = resolver.getChecksum(Locale.ENGLISH);

        // verify
        assertNotEquals(before, after);
    }

    @Test
    void test_getClientTexts_nullLocaleThrows()
    {
        // setup
        AbsTranslationProvider provider = provider();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> provider.getClientTexts(null));
    }

    @Test
    void test_resolveFallbacks_containsClientDefaults()
    {
        // setup
        UiTranslationResolver resolver = new UiTranslationResolver(provider());
        UiTranslations        result;

        // run
        result = resolver.resolveFallbacks();

        // verify
        assertEquals(BasicText.UI_F5_BLOCKED.getText(), result.texts().get(BasicText.UI_F5_BLOCKED.getKey()));
        assertEquals(BasicText.UI_INPUT_PATTERN_MISMATCH.getText(), result.texts().get(BasicText.UI_INPUT_PATTERN_MISMATCH.getKey()));
    }

    @Test
    void test_resolveFallbacks_omitsServerScopedDefaults()
    {
        // setup
        UiTranslationResolver resolver = new UiTranslationResolver(provider());
        UiTranslations        result;

        // run
        result = resolver.resolveFallbacks();

        // verify
        assertFalse(result.texts().containsKey(BasicText.OK.getKey()));
    }

    @Test
    void test_getFallbackChecksum_isStableAndIndependentOfProvider()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        String                 before   = resolver.getFallbackChecksum();
        String                 after;

        provider.addTranslation(Locale.ENGLISH, BasicText.UI_F5_BLOCKED.getKey(), "custom", TranslationScope.CLIENT);

        // run
        after = resolver.getFallbackChecksum();

        // verify
        assertEquals(before, after);
        assertEquals(64, after.length());
    }

    @Test
    void test_resolveFallbacks_ignoresProviderTexts()
    {
        // setup
        AbsTranslationProvider provider = provider();
        UiTranslationResolver  resolver = new UiTranslationResolver(provider);
        UiTranslations         result;

        provider.addTranslation(Locale.ENGLISH, BasicText.UI_F5_BLOCKED.getKey(), "custom", TranslationScope.CLIENT);

        // run
        result = resolver.resolveFallbacks();

        // verify
        assertEquals(BasicText.UI_F5_BLOCKED.getText(), result.texts().get(BasicText.UI_F5_BLOCKED.getKey()));
    }
}
