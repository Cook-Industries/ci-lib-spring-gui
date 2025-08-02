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

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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

    private static final Set<String> STATIC_FOLDERS = Set.of("css", "js", "images");

    private final CiLibProperties    properties;

    public CiLibStaticResourceConfig(CiLibProperties properties)
    {
        this.properties = properties;
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
        String basePath = properties.getWeb().getPath().getStaticWebResources();
        Path   path     = Path.of(basePath);

        try
        {
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
        ClassPathResource resource   = new ClassPathResource("cook-industries/js/ci-lib-spring-web.js");

        File              targetFile = new File(basePath + "/js/" + resource.getFilename());

        try (InputStream in = resource.getInputStream())
        {
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry)
    {
        Path   resourcePath   = Path.of(properties.getWeb().getPath().getStaticWebResources()).toAbsolutePath();

        String osName         = System.getProperty("os.name").toLowerCase();

        String osFileAddition = osName.contains("win") ? "//" : "";

        String location       = String.format("file:/%s%s/", osFileAddition, resourcePath.toString());

        registry
            .addResourceHandler("/**")
            .addResourceLocations(location, "classpath:/static/");
    }

}
