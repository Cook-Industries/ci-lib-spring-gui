/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Locale;
import java.util.function.Supplier;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor.UploadStarter;
import de.cookindustries.lib.spring.gui.hmi.util.GUIFactory;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.StartFileUploadResponse;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.StartResult;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionLimits;

/**
 * Creates {@link InputExtractor}s with the application's file upload setup, if available.
 * <p>
 * The upload setup is active if the application context holds an {@code UploadSessionStore} and an {@code AbstractReceiver}. Without it, the
 * extractors are created without upload support: {@link InputExtractor#approve(String, java.util.function.Supplier)} throws an
 * {@code IllegalStateException}. Announced file sizes are checked against {@link UploadSessionLimits} either way.
 *
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public final class InputExtractorFactory
{

    private final GUIFactory          guiFactory;
    private final UploadStarter       starter;
    private final String              uploadUrl;
    private final UploadSessionLimits limits;

    public InputExtractorFactory(GUIFactory guiFactory, @Nullable FileUploadCoordinator coordinator, CiLibProperties properties,
        UploadSessionLimits limits)
    {
        this.guiFactory = guiFactory;
        this.starter = coordinator == null
            ? null
            : coordinator::startUpload;
        this.uploadUrl = properties.getWeb().getUpload().getUrl();
        this.limits = limits;
    }

    /**
     * Create a extractor for the inputs of a {@code Form} request.
     * 
     * @param inputs map from the {@code Form}
     * @param locale to fetch error messages with
     * @param fromDataHolderRefSupplier reference to the form data result object
     * @return a new extractor
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    public <R> InputExtractor<R> create(MultiValueMap<String, String> inputs, Locale locale, Supplier<R> fromDataHolderRefSupplier)
    {
        return new InputExtractor<R>(this, inputs, locale, starter, limits.maxFileLength(), limits.maxBatchLength(),
            fromDataHolderRefSupplier);
    }

    NotificationResponse buildFailedInputCheckResponse(InputExtractor<?> extractor)
    {
        return guiFactory.createNotificationActiveMarkerResponse(extractor);
    }

    StartFileUploadResponse buildFileUploadStartResponse(StartResult result, String completionHandlerKey)
    {
        return StartFileUploadResponse
            .builder()
            .batchUuid(result.batchUuid())
            .uploadUrl(uploadUrl)
            .uploadPolicy(result.policy())
            .uploadLimits(limits)
            .fieldDescribtors(result.files())
            .build();
    }
}
