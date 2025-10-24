/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.html.CssAnimation;
import de.cookindustries.lib.spring.gui.html.CssConfig;
import de.cookindustries.lib.spring.gui.html.CssEntity;
import de.cookindustries.lib.spring.gui.util.ResourceLoader;

@Component
final class CiLibStaticFileCreator implements ApplicationRunner
{

    private static final Logger   LOG = LoggerFactory.getLogger(CiLibStaticFileCreator.class);

    private final CiLibProperties properties;

    public CiLibStaticFileCreator(CiLibProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        createCssBaseFile();
    }

    /**
     * Create or override the base CSS file for the library core functions.
     * <p>
     * Incorporate the modifications from a user file expected under {@code resources/config/css-config.json}.
     */
    private void createCssBaseFile()
    {
        LOG.debug("create static css file");

        CssConfig cssConfig    = null;
        CssConfig staticConfig =
            ResourceLoader.loadJsonFrom("cook-industries/config/cook-industries-core-css-config.json", CssConfig.class);

        CssConfig userConfig   = null;

        try
        {
            userConfig = ResourceLoader.loadJsonFrom(FileAndFolderPaths.USER_FILE_CONFIG_CSS, CssConfig.class);

            LOG.debug("found css-config to load");
        }
        catch (Exception ex)
        {
            LOG.warn("could not load/find css-config");
        }

        if (userConfig == null)
        {
            cssConfig = staticConfig;
        }
        else
        {
            Map<String, CssEntity>    cssRules      = new HashMap<>();
            Map<String, CssAnimation> cssAnimations = new HashMap<>();

            staticConfig.getRules().forEach(rule -> cssRules.put(rule.getSelector(), rule));
            userConfig.getRules().forEach(rule -> cssRules.put(rule.getSelector(), rule));

            staticConfig.getAnimations().forEach(anim -> cssAnimations.put(anim.getName(), anim));
            userConfig.getAnimations().forEach(anim -> cssAnimations.put(anim.getName(), anim));

            cssConfig =
                CssConfig
                    .builder()
                    .rules(cssRules.values())
                    .animations(cssAnimations.values())
                    .build();
        }

        Path filePath =
            Path.of(properties.getWeb().getPath().getStaticWebResources(), "css", "cook-industries-core.css");

        try
        {
            if (!Files.exists(filePath))
            {
                Files.createFile(filePath);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            cssConfig.getRules()
                .stream()
                .sorted(CssEntity.compareByFirstChar())
                .forEach(rule -> {
                    try
                    {
                        writer.write(rule.toCssString());
                        writer.newLine();
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                });

            cssConfig.getAnimations()
                .stream()
                .forEach(animation -> {
                    try
                    {
                        writer.write(animation.toCssString());
                        writer.newLine();
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                });
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

}
