/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Translation provider to load JSON files from resources
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class ResourceTranslationProvider extends AbsTranslationProvider
{

    private final List<String> paths;
    private final ObjectMapper mapper = new ObjectMapper();

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
                TranslationMapping mapping = map(path);

                toString();
                Locale locale = new Locale(mapping.getLanguage(), mapping.getCountry());

                for (TranslationMappingText text : mapping.getElements())
                {
                    addTranslation(locale, text.getKey(), text.getText());
                }
            }
            catch (Exception ex)
            {
                // TODO: write exception
            }
        }

        return this;
    }

    private TranslationMapping map(String path) throws IOException
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);

        return mapper.readValue(inputStream, TranslationMapping.class);
    }
}
