/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi;

import java.util.Locale;

import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeRoot;
import de.cookindustries.lib.spring.gui.hmi.mapper.ValueMap;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.Response;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public final class GUIFactory
{

    private final AbsTranslationProvider translationProvider;

    public GUIFactory(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Read a static template and transform into a {@link Container}
     * 
     * @param path to read in
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path)
    {
        try
        {
            JsonTreeMapper mapper = new JsonTreeMapper();
            JsonTreeRoot   root   = mapper.map(path);

            return JsonMapper.map(root);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }

    /**
     * Read a dynamic template and transform into a {@link Container}
     * 
     * @param path to read in
     * @param valueMap dynamic {@code valueMap}
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path, Locale locale, ValueMap... valueMap)
    {
        try
        {
            JsonTreeMapper mapper = new JsonTreeMapper();
            JsonTreeRoot   root   = mapper.map(path);

            return JsonMapper.map(root, locale, translationProvider, valueMap);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }

    /**
     * @param inputExtractor
     * @return a {@link NotificationResponse} containing the marker raised by {@link InputExtractor}
     */
    public Response getActiveMarkerResponse(InputExtractor inputExtractor)
    {
        return NotificationResponse
            .builder()
            .messages(inputExtractor.getMessages())
            .build();
    }
}
