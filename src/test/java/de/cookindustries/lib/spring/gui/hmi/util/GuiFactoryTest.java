/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesPathProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesProperties;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;
import de.cookindustries.lib.spring.gui.response.ContentHandling;
import de.cookindustries.lib.spring.gui.response.ContentResponse;
import de.cookindustries.lib.spring.gui.util.TestHelper;

public class GuiFactoryTest
{

    private static final List<String> HTML_STRINGS =
        List.of(
            "<!DOCTYPE html>",
            "<base href=\"/\">",
            "<title>",
            "</title>",
            "<link href=\"/webjars/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">",
            "<link href=\"/webjars/bootstrap-icons/font/bootstrap-icons.css\" rel=\"stylesheet\">",
            "<link href=\"/webjars/yaireo__tagify/dist/tagify.css\" rel=\"stylesheet\">",
            "<link href=\"/css/cook-industries-core.css\" rel=\"stylesheet\">",
            "<script type=\"importmap\">{\"imports\": {\"cilib\": \"/js/ci-lib-spring-web.js\"}}</script>",
            "<script src=\"/webjars/jquery/jquery.min.js\"></script>",
            "<script src=\"/webjars/bootstrap/js/bootstrap.min.js\"></script>",
            "<script src=\"/webjars/yaireo__tagify/dist/tagify.js\"></script>",
            "<script src=\"/webjars/stomp__stompjs/esm6/index.js\"></script>",
            "<p id=\"ui-properties-url\" class=\"hidden\" ></p>",
            "<div id=\"modal-container\" class=\"hidden\"></div>",
            "<div id=\"global-loader-overlay\" class=\"d-flex justify-content-center align-items-center vh-100\"><div id=\"global-loader\" class=\"d-flex flex-column justify-items-center align-items-center\"><div id=\"_lcsptp\" class=\"loader-circle spinner-border text-primary\"></div><p id=\"global-loader-text\" class=\"loader-text\" >loading...</p><div id=\"global-loader-bar\" class=\"loadbar\"></div></div></div>",
            "<div id=\"error-overlay\" class=\"hidden\"><div id=\"error-container\"><div id=\"error-holder\"></div><button id=\"_eob\" onclick=\"CILIB.FunctionRegistry.call('dismissErrors');\" class=\"btn btn-primary\">OK</button></div></div>",
            "<div id=\"popup-holder\"></div>");

    private TemplateFileCache         templateFileCache;
    private FlatMappableDissector     flatMappableDissector;
    private AbsTranslationProvider    translationProvider;
    private GuiFactoryProperties      properties;

    @BeforeEach
    public void init()
    {
        CiLibResourcesPathProperties resPath = new CiLibResourcesPathProperties();
        resPath.setTemplates("json-test-files");

        CiLibResourcesProperties res = new CiLibResourcesProperties();
        res.setPath(resPath);

        CiLibProperties libProperties = new CiLibProperties();
        libProperties.setResources(res);

        templateFileCache = new TemplateFileCache(libProperties);
        templateFileCache.init();

        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider());

        translationProvider = new StaticTranslationProvider();

        properties = GuiFactoryProperties.builder().build();
    }

    private void checkAgainstBasicStrings(String html)
    {
        List<Runnable> asserts = new ArrayList<>();

        HTML_STRINGS.forEach(line -> {
            asserts.add(() -> assertTrue(html.contains(line), String.format("expected [%s]", line)));
        });

        TestHelper.multiAssert(asserts);
    }

    private GUIFactory basicFactory()
    {
        return new GUIFactory(properties, templateFileCache, translationProvider, flatMappableDissector);
    }

    private boolean checkTags(String tag, String html)
    {
        int count = 0;
        int index = 0;

        while ((index = html.indexOf(tag, index)) != -1)
        {
            count++;
            index += tag.length();
        }

        return count % 2 == 0;
    }

    @Test
    void test_constructor()
    {
        // setup
        GUIFactory factory = basicFactory();

        // run & verify
        assertNotNull(factory);
    }

    @Test
    void test_createLocale_language()
    {
        // setup
        GUIFactory factory = basicFactory();

        // run
        Locale     locale1 = factory.createLocale("de");
        Locale     locale2 = factory.createLocale("de_DE");

        // verify
        assertEquals(new Locale("de"), locale1);
        assertEquals(new Locale("de", "DE"), locale2);
    }

    @Test
    void test_getTranslatedText()
    {
        // setup
        GUIFactory factory = basicFactory();

        // run
        String translated = factory.getTranslatedText(Locale.ENGLISH, "test");

        // verify
        assertEquals("I18N [test] not set.", translated);
    }

    @Test
    void test_createHtmlSite()
    {
        // setup
        String           title   = "Test Site";
        ComponentSources compSrc =
            ComponentSources
                .builder()
                .locale(Locale.ENGLISH)
                .sourcePath("json-mapper/root-component.json")
                .build();
        GUIFactory       factory = basicFactory();

        // run
        String           content = factory.createHtmlSite(title, compSrc);

        // verify
        assertNotNull(content);
        // must be false since this line exists <!DOCTYPE html>
        assertFalse(checkTags("html", content));
        assertTrue(checkTags("body>", content));
        checkAgainstBasicStrings(content);
    }

    @Test
    void test_createHtmlSite_withTranslatedTitle()
    {
        // setup
        String           title   = "$$Test";
        ComponentSources compSrc =
            ComponentSources
                .builder()
                .locale(Locale.ENGLISH)
                .sourcePath("json-mapper/root-component.json")
                .build();
        GUIFactory       factory = basicFactory();

        // run
        String           content = factory.createHtmlSite(title, compSrc);

        // verify
        assertNotNull(content);
        // must be false since this line exists <!DOCTYPE html>
        assertFalse(checkTags("html", content));
        assertTrue(checkTags("body>", content));
        checkAgainstBasicStrings(content);
    }

    @Test
    void test_createComponentResponse()
    {
        // setup
        String           elementId = "test-id";
        ComponentSources compSrc   =
            ComponentSources
                .builder()
                .locale(Locale.ENGLISH)
                .sourcePath("json-mapper/root-component.json")
                .build();
        GUIFactory       factory   = basicFactory();

        // run
        ContentResponse  content   = factory.createComponentResponse(elementId, ContentHandling.APPEND, compSrc);

        // verify
        assertNotNull(content);
    }
}
