/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
        LOG.info("init [{}]", TemplateFileCache.class.getSimpleName());

        ObjectMapper mapper = new ObjectMapper();
        int          expected;
        int          cnt    = 0;

        try
        {
            PathMatchingResourcePatternResolver resolver   = new PathMatchingResourcePatternResolver();

            String                              searchPath = "classpath*:" + path + "**/*.json";

            LOG.debug("load resources under [{}]", searchPath);

            Resource[] resources = resolver.getResources(searchPath);

            expected = resources.length;

            for (Resource resource : resources)
            {
                LOG.debug("load resource [{}]", resource.getURI());

                try (InputStream is = resource.getInputStream())
                {
                    String fullPath     = resource.getURL().getPath();

                    int    index        = fullPath.indexOf(path);
                    String relativePath = index >= 0 ? fullPath.substring(index + path.length()) : resource.getFilename();

                    LOG.debug("read template [{}]", relativePath);

                    PseudoElement element = mapper.readValue(is, PseudoElement.class);

                    templateMap.put(relativePath, element);

                    cnt++;
                }
                catch (IOException ex)
                {
                    LOG.error("error reading json from resource: " + resource, ex);
                }
            }
        }
        catch (Exception ex)
        {
            LOG.error("error initializing TemplateFileCache", ex);

            throw new RuntimeException(ex);
        }

        LOG.info("init finished with [{}] files from [{}] expected", cnt, expected);
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
