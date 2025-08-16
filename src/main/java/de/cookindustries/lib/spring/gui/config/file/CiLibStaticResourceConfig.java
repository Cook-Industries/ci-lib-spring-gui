/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import jakarta.annotation.PostConstruct;

/**
 * Configuration to create a folder structure from where the static web content can be served.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Configuration
public class CiLibStaticResourceConfig implements WebMvcConfigurer
{

    private static final Logger                       LOG            = LoggerFactory.getLogger(CiLibStaticResourceConfig.class);

    private static final Set<String>                  STATIC_FOLDERS = Set.of("css", "js", "images");

    private final CiLibProperties                     properties;
    private final PathMatchingResourcePatternResolver pathResolver;

    public CiLibStaticResourceConfig(CiLibProperties properties)
    {
        this.properties = properties;
        this.pathResolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * Create the following structure at the user defined {@code base path}:
     * <ul>
     * <li>{@code /css}</li>
     * <li>{@code /js}</li>
     * <li>{@code /images}</li>
     * </ul>
     * <p>
     * Additionally copy the file {@code ci-lib-spring-web.js} to the {@code /js} folder.
     */
    @PostConstruct
    public void init()
    {
        LOG.info("init [{}]", CiLibStaticResourceConfig.class.getSimpleName());

        String basePath = properties.getWeb().getPath().getStaticWebResources();
        Path   path     = Path.of(basePath);

        try
        {
            LOG.debug("create static resource folder [{}]", path);

            Files.createDirectories(path);

            STATIC_FOLDERS.forEach(folder -> {
                Path folderPath = Path.of(basePath, folder);

                try
                {
                    Files.createDirectories(folderPath);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException("Failed to create static resource folder: " + path, ex);
                }
            });

            copyJsLib(basePath);
            copyImages(basePath);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to create static resource directory: " + path, ex);
        }
    }

    /**
     * Copy the file {@code ci-lib-spring-web.js} from the internal resources to the static content folder {@code /js}
     * 
     * @param basePath to the satics folder
     * @throws IOException if the file could not be copied
     */
    private void copyJsLib(String basePath) throws IOException
    {
        try
        {
            Resource resource   = pathResolver.getResource("classpath:cook-industries/js/ci-lib-spring-web.js");

            File     targetFile = new File(basePath + "/js/" + resource.getFilename());

            LOG.debug("copy JsLib to [{}]", targetFile);

            try (InputStream in = resource.getInputStream())
            {
                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to copy static resource JsLib", ex);
        }
    }

    private void copyImages(String basePath) throws IOException
    {
        try
        {
            Resource[] resources = pathResolver.getResources("classpath*:cook-industries/images/*");

            for (Resource resource : resources)
            {
                File targetFile = new File(basePath + "/images/" + resource.getFilename());

                LOG.debug("copy image to [{}]", targetFile);

                try (InputStream in = resource.getInputStream())
                {
                    Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to copy static resources from images", ex);
        }
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry)
    {
        Path   resourcePath    = Path.of(properties.getWeb().getPath().getStaticWebResources()).toAbsolutePath().normalize();

        String location        = resourcePath.toUri().toString();
        String staticClasspath = "classpath:/static/";

        LOG.debug("register [{}] and [{}] as static file paths", location, staticClasspath);

        registry
            .addResourceHandler("/**")
            .addResourceLocations(location, staticClasspath);
    }

}
