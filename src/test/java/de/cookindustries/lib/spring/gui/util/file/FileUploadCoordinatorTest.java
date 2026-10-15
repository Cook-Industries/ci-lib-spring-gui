/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.StartResult;

class FileUploadCoordinatorTest
{

    private static final String HANDLER_KEY = "test-handler";

    record TestForm(String name)
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

    static final class ClaimRecordingStore implements UploadSessionStore
    {

        BatchInfo batch;

        Instant   claimedDeadline;

        @Override
        public void createBatch(BatchInfo batch, List<FileInit> files)
        {
            this.batch = batch;
        }

        @Override
        public Optional<BatchInfo> findBatch(UUID batchUuid)
        {
            return Optional.ofNullable(batch);
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
            return true;
        }

        @Override
        public boolean tryClaimCompletion(UUID batchUuid, Instant deadline)
        {
            claimedDeadline = deadline;

            return true;
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

    @Test
    void test_complete_claimsCompletionWithDeadline()
    {
        // setup
        Duration              completionTimeout = Duration.ofMinutes(7);
        ClaimRecordingStore   store             = new ClaimRecordingStore();
        UploadSessionLimits   limits            = new UploadSessionLimits(1_000L, 1_500L, Duration.ofMinutes(10), completionTimeout);
        FileUploadCoordinator coordinator       =
            new FileUploadCoordinator(
                store,
                receiver(),
                new NoOpFormCipher(),
                List.<CompletionHandler<?, ?>>of(new TestHandler()),
                new UploadSessionPolicy(new CiLibProperties()),
                limits);
        StartResult           started           = coordinator.startUpload(HANDLER_KEY, () -> new TestForm("name"), List.of());
        Instant               before            = Instant.now();

        // run
        coordinator.complete(started.batchUuid());

        // verify
        assertFalse(store.claimedDeadline.isBefore(before.plus(completionTimeout)));
        assertFalse(store.claimedDeadline.isAfter(Instant.now().plus(completionTimeout)));
    }

}
