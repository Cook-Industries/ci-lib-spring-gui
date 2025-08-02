/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.PseudoElement;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import jakarta.annotation.PostConstruct;

/**
 * A global cache for {@code template} files.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public class TemplateFileCache
{

    private static final Logger              LOG = LoggerFactory.getLogger(TemplateFileCache.class);

    private final String                     path;
    private final Map<String, PseudoElement> templateMap;

    /**
     * Create a new file cache.
     *
     * @param properties to use
     */
    public TemplateFileCache(CiLibProperties properties)
    {
        templateMap = new ConcurrentHashMap<>();

        String rawPath = properties.getResources().getPath().getTemplates();
        path = rawPath.endsWith("/") ? rawPath : rawPath + "/";
    }

    /**
     * Init all json files under a set path.
     */
    @PostConstruct
    public void init()
    {
        LOG.debug("init [{}]", TemplateFileCache.class.getSimpleName());

        ObjectMapper mapper = new ObjectMapper();

        try
        {
            URL resourceUrl = getClass().getClassLoader().getResource(path);
            if (resourceUrl == null)
            {
                throw new IllegalArgumentException(String.format("Resource folder not found on classpath: [%s]", path));
            }

            Path basePath = Paths.get(resourceUrl.toURI());

            try (Stream<Path> paths = Files.walk(basePath))
            {
                paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> {
                        try
                        {
                            String relativePath = StringAdapter.sanitizePath(basePath.relativize(p).toString());

                            LOG.debug("read template [{}]", relativePath);

                            PseudoElement element = mapper.readValue(p.toFile(), PseudoElement.class);

                            templateMap.put(relativePath, element);
                        }
                        catch (IOException ex)
                        {
                            LOG.error("error reading json", ex);
                        }
                    });
            }
            catch (IOException ex)
            {
                LOG.error("error accessing resources", ex);

                new RuntimeException(ex);
            }
        }
        catch (Exception ex)
        {
            LOG.error("error initializing TemplateFileCache", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get a {@code template} to a specific {@code path};
     * 
     * @param path to lookup
     * @return the {@code template}
     * @throws IllegalArgumentException if no mapping could be found
     */
    public PseudoElement getTemplateTo(String path)
    {
        String                  sanPath  = StringAdapter.sanitizePath(path);
        Optional<PseudoElement> template = Optional.ofNullable(templateMap.get(sanPath));

        return template.orElseThrow(() -> new IllegalArgumentException(String.format("no template for path [%s]", sanPath)));
    }
}
