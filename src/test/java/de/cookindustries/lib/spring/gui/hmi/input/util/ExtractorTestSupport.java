/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesPathProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesProperties;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.util.GUIFactory;
import de.cookindustries.lib.spring.gui.hmi.util.GuiFactoryProperties;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.util.file.AbstractReceiver;
import de.cookindustries.lib.spring.gui.util.file.CompletionHandler;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator;
import de.cookindustries.lib.spring.gui.util.file.NoOpFormCipher;
import de.cookindustries.lib.spring.gui.util.file.UploadBatchState;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionLimits;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionPolicy;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore;
import de.cookindustries.lib.spring.gui.util.file.UploadedFile;

/**
 * Wires a real {@link InputExtractorFactory} on top of a {@link FileUploadCoordinator} with a recording store, so tests can follow an
 * extractor all the way into the upload start.
 */
final class ExtractorTestSupport
{

    static final String HANDLER_KEY = "test-handler";

    static final String UPLOAD_URL  = "/api/uploads";

    static final long   MAX_FILE    = 1_000L;

    static final long   MAX_BATCH   = 1_500L;

    private ExtractorTestSupport()
    {
    }

    public record TestForm(String name)
    {
    }

    static final class TestHandler implements CompletionHandler<TestForm, Response>
    {

        @Override
        public String completeKey()
        {
            return HANDLER_KEY;
        }

        @Override
        public Class<TestForm> formType()
        {
            return TestForm.class;
        }

        @Override
        public Response onComplete(TestForm formData, List<UploadedFile> files)
        {
            return null;
        }
    }

    /**
     * Keeps what {@code createBatch} received, everything else is inert.
     */
    static final class RecordingStore implements UploadSessionStore
    {

        BatchInfo       batch;

        List<FileInit>  files;

        @Override
        public void createBatch(BatchInfo batch, List<FileInit> files)
        {
            this.batch = batch;
            this.files = files;
        }

        @Override
        public Optional<BatchInfo> findBatch(UUID batchUuid)
        {
            return Optional.empty();
        }

        @Override
        public Optional<FileInfo> findFile(UUID fileUuid)
        {
            return Optional.empty();
        }

        @Override
        public List<FileInfo> findByBatch(UUID batchUuid)
        {
            return List.of();
        }

        @Override
        public PartResult registerPart(UUID fileUuid, int partNumber, long size)
        {
            return PartResult.ADDED;
        }

        @Override
        public boolean markFileFinished(UUID fileUuid)
        {
            return false;
        }

        @Override
        public boolean transition(UUID batchUuid, UploadBatchState from, UploadBatchState to)
        {
            return false;
        }

        @Override
        public boolean touch(UUID batchUuid, Instant newExpiry)
        {
            return false;
        }

        @Override
        public List<UUID> claimExpired(Instant now, int limit)
        {
            return List.of();
        }

        @Override
        public void delete(UUID batchUuid)
        {
        }
    }

    static final class Fixture
    {

        final CiLibProperties        properties   = properties();

        final UploadSessionLimits    limits       = new UploadSessionLimits(MAX_FILE, MAX_BATCH, Duration.ofMinutes(10));

        final UploadSessionPolicy    policy       = new UploadSessionPolicy(properties);

        final RecordingStore         store        = new RecordingStore();

        final AbsTranslationProvider translations = new StaticTranslationProvider();

        final InputExtractorFactory  factory;

        Fixture()
        {
            GUIFactory            guiFactory =
                new GUIFactory(
                    GuiFactoryProperties.builder().build(),
                    new TemplateFileCache(properties),
                    translations,
                    new FlatMappableDissector(translations, properties));

            FileUploadCoordinator coordinator =
                new FileUploadCoordinator(
                    store,
                    receiver(),
                    new NoOpFormCipher(),
                    List.<CompletionHandler<?, ?>>of(new TestHandler()),
                    policy,
                    limits);

            factory = new InputExtractorFactory(guiFactory, coordinator, properties, limits);
        }
    }

    static CiLibProperties properties()
    {
        CiLibResourcesPathProperties resPath = new CiLibResourcesPathProperties();
        resPath.setTemplates("json-test-files");

        CiLibResourcesProperties res = new CiLibResourcesProperties();
        res.setPath(resPath);

        CiLibProperties props = new CiLibProperties();
        props.setResources(res);
        props.getWeb().getUpload().setUrl(UPLOAD_URL);

        return props;
    }

    private static AbstractReceiver receiver()
    {
        return new AbstractReceiver() {

            @Override
            protected void doReceive(UUID fileUuid, int partNumber, long offset, long length, InputStream in)
            {
            }

            @Override
            protected void doFinish(UUID fileUuid)
            {
            }

            @Override
            protected void doAbort(UUID fileUuid)
            {
            }
        };
    }
}
