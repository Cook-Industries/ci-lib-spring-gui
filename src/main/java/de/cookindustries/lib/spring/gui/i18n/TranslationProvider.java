/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TranslationProvider
{

    private final ObjectMapper mapper = new ObjectMapper();

    private TranslationMap     translationMap;

    public void initTranslationMapStatic(List<String> paths)
    {
        translationMap = new TranslationMap();

        for (String path : paths)
        {
            try
            {
                TranslationMapping mapping = map(path);

                Locale             locale  = new Locale(mapping.getLanguage(), mapping.getCountry());

                for (TranslationMappingText text : mapping.getElements())
                {
                    translationMap.addTranslation(locale, text.getKey(), text.getText());
                }
            }
            catch (Exception ex)
            {
                // TODO: write exception
            }
        }

        translationMap.seal();
    }

    public TranslationMap getTranslationMap()
    {
        return translationMap;
    }

    private TranslationMapping map(String path) throws IOException
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);

        return mapper.readValue(inputStream, TranslationMapping.class);
    }
}
