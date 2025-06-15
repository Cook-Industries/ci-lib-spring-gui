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
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeRoot;
import de.cookindustries.lib.spring.gui.hmi.mapper.ValueMap;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public class GUIFactory
{

    private final AbsTranslationProvider translationProvider;

    public GUIFactory(AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;
    }

    /**
     * Read in a static template and transform into a {@link Container}
     * 
     * @param path to read in
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }

    /**
     * Read in a dynamic template and transform into a {@link Container}
     * 
     * @param path to read in
     * @param valueMap dynamic {@code valueMap}
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path, Locale locale, ValueMap... valueMap)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root, locale, translationProvider, valueMap);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }
}
