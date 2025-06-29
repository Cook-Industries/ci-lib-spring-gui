/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Translation provider to load JSON files from resources
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class ResourceTranslationProvider extends AbsTranslationProvider
{

    private static final Logger LOG    = LoggerFactory.getLogger(ResourceTranslationProvider.class);

    private final List<String>  paths;
    private final ObjectMapper  mapper = new ObjectMapper();

    /**
     * Construct a new instance
     * 
     * @param paths a list of relative paths to the {@code resources} folder
     */
    public ResourceTranslationProvider(List<String> paths)
    {
        this.paths = paths;
    }

    @Override
    public final AbsTranslationProvider initMaps()
    {
        for (String path : paths)
        {
            try
            {
                InputStream        inputStream = ResourceTranslationProvider.class.getClassLoader().getResourceAsStream(path);

                TranslationMapping mapping     = mapper.readValue(inputStream, TranslationMapping.class);;

                // TODO: remove this when update to 17+ is done
                @SuppressWarnings("deprecation")
                Locale locale = new Locale(mapping.getLanguage(), mapping.getCountry());

                for (TranslationMappingText text : mapping.getTexts())
                {
                    try
                    {
                        addTranslation(locale, text.getKey(), text.getText());
                    }
                    catch (Exception ex)
                    {
                        LOG.error("error while reading translation entry [{}] - [{}]", text, ex);
                    }
                }
            }
            catch (Exception ex)
            {
                LOG.error("error while reading translation resource [{}] - [{}]", path, ex);
            }
        }

        return this;
    }

}
