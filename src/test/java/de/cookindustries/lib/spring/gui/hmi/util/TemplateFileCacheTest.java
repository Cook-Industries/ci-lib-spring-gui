/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesPathProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesProperties;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.PseudoElement;

class TemplateFileCacheTest
{

    private ClassLoader    originalLoader;
    private URLClassLoader templateLoader;

    @BeforeEach
    void init()
    {
        originalLoader = Thread.currentThread().getContextClassLoader();
    }

    @AfterEach
    void cleanup() throws Exception
    {
        Thread.currentThread().setContextClassLoader(originalLoader);

        if (templateLoader != null)
        {
            templateLoader.close();
        }
    }

    private CiLibProperties propertiesFor(String templatesPath)
    {
        CiLibResourcesPathProperties resPath = new CiLibResourcesPathProperties();
        resPath.setTemplates(templatesPath);

        CiLibResourcesProperties res = new CiLibResourcesProperties();
        res.setPath(resPath);

        CiLibProperties properties = new CiLibProperties();
        properties.setResources(res);

        return properties;
    }

    @Test
    void test_init_parentFolderNamedLikeRoot_keepsRelativePath(@TempDir Path tempDir) throws Exception
    {
        // setup
        Path              classesDir = tempDir.resolve("templates/app/classes");
        Path              template   = classesDir.resolve("templates/sub/page.ged");
        TemplateFileCache cache;
        PseudoElement     result;

        Files.createDirectories(template.getParent());
        Files.writeString(template, "{\"type\": \"content\"}");

        templateLoader = new URLClassLoader(new URL[] { classesDir.toUri().toURL() }, null);
        Thread.currentThread().setContextClassLoader(templateLoader);

        cache = new TemplateFileCache(propertiesFor("templates"));

        // run
        cache.init();
        result = cache.getTemplateTo("sub/page.ged");

        // verify
        assertNotNull(result);
    }

    @Test
    void test_init_nestedFolderNamedLikeRoot_keepsRelativePath(@TempDir Path tempDir) throws Exception
    {
        // setup
        Path              classesDir = tempDir.resolve("classes");
        Path              template   = classesDir.resolve("templates/sub/templates/page.ged");
        TemplateFileCache cache;
        PseudoElement     result;

        Files.createDirectories(template.getParent());
        Files.writeString(template, "{\"type\": \"content\"}");

        templateLoader = new URLClassLoader(new URL[] { classesDir.toUri().toURL() }, null);
        Thread.currentThread().setContextClassLoader(templateLoader);

        cache = new TemplateFileCache(propertiesFor("templates"));

        // run
        cache.init();
        result = cache.getTemplateTo("sub/templates/page.ged");

        // verify
        assertNotNull(result);
    }
}
