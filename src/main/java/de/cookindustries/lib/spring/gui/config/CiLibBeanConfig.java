/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.cookindustries.lib.spring.gui.hmi.util.GuiFactoryProperties;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Configuration
public class CiLibBeanConfig
{

    /**
     * Default constructor
     */
    public CiLibBeanConfig()
    {
        // Default constructor
    }

    @Bean
    @ConditionalOnMissingBean(GuiFactoryProperties.class)
    GuiFactoryProperties createGuiFactoryProperties()
    {
        return GuiFactoryProperties
            .builder()
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(AbsTranslationProvider.class)
    AbsTranslationProvider translationProvider()
    {
        return new StaticTranslationProvider();
    }

}
