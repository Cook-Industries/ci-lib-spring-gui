/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.hmi.util.GuiFactoryProperties;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.UiTranslationResolver;
import de.cookindustries.lib.spring.gui.util.file.AbstractFormCipher;
import de.cookindustries.lib.spring.gui.util.file.AbstractReceiver;
import de.cookindustries.lib.spring.gui.util.file.CompletionHandler;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator;
import de.cookindustries.lib.spring.gui.util.file.NoOpFormCipher;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionLimits;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionPolicy;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Configuration
class CiLibBeanConfig
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

    @Bean
    UiTranslationResolver uiTranslationResolver(AbsTranslationProvider translationProvider)
    {
        return new UiTranslationResolver(translationProvider);
    }

    @Bean
    @ConditionalOnMissingBean(UploadSessionPolicy.class)
    UploadSessionPolicy uploadSessionPolicy(CiLibProperties properties)
    {
        return new UploadSessionPolicy(properties);
    }

    @Bean
    @ConditionalOnMissingBean(UploadSessionLimits.class)
    UploadSessionLimits uploadSessionLimits(CiLibProperties properties)
    {
        return new UploadSessionLimits(
            properties.getWeb().getUpload().getLimits().getMaxFileSize().toBytes(),
            properties.getWeb().getUpload().getLimits().getMaxBatchSize().toBytes(),
            properties.getWeb().getUpload().getLimits().getMaxTtl(),
            properties.getWeb().getUpload().getLimits().getCompletionTimeout());
    }

    @Bean
    @ConditionalOnMissingBean(AbstractFormCipher.class)
    AbstractFormCipher noopFormCipher()
    {
        return new NoOpFormCipher();
    }

    @Bean
    @ConditionalOnBean({ UploadSessionStore.class, AbstractReceiver.class })
    FileUploadCoordinator fileUploadCoordinator(UploadSessionStore store, AbstractReceiver receiver, AbstractFormCipher cipher,
        List<CompletionHandler<?, ?>> handlers, UploadSessionPolicy policy, UploadSessionLimits limits)
    {
        return new FileUploadCoordinator(store, receiver, cipher, handlers, policy, limits);
    }

}
