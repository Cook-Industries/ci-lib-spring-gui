/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence SPI for chunked upload sessions.
 * <p>
 * Design rule: this interface contains only <b>small primitives</b>. Every method that decides a race ({@link #registerPart},
 * {@link #markFileFinished}, {@link #transition}, {@link #claimExpired}) returns the <b>outcome of the winning/losing</b> as its return
 * value. Callers must act only on that return value, never on a prior read ("check, then write" is always wrong when several instances
 * share one store).
 * <p>
 * All business logic (validation, state machine, "are all parts there?") lives in the coordinator, not here. An implementation therefore
 * only has to map each method onto <b>one atomic step</b> of its backend (one conditional SQL statement, one Lua script, one compareAndSet,
 * ...).
 * <p>
 * Every implementation must pass {@code AbstractUploadSessionStoreTest} against the real backend.
 */
public interface UploadSessionStore
{

    enum PartResult
    {
        ADDED, DUPLICATE
    }

    /**
     * input for creating a file entry
     */
    record FileInit(UUID fileUuid, String fieldId, long expectedLength)
    {
    }

    /**
     * store view of a file; submittedLength is the sum of all registered parts
     */
    record FileInfo(UUID fileUuid, UUID batchUuid, String fieldId, long expectedLength, long submittedLength, boolean finished)
    {
    }

    /**
     * store view of a batch; encryptedFormPojo is opaque to the store
     */
    record BatchInfo(UUID batchUuid, UploadBatchState status, long partLength, String completeKey, String keyId,
        byte[] encryptedFormPojo, Instant expiresAt)
    {
    }

    /**
     * Creates batch and all its files as one unit (all or nothing).
     */
    void createBatch(BatchInfo batch, List<FileInit> files);

    Optional<BatchInfo> findBatch(UUID batchUuid);

    Optional<FileInfo> findFile(UUID fileUuid);

    List<FileInfo> findByBatch(UUID batchUuid);

    /**
     * Registers a received part exactly once.
     *
     * @return {@link PartResult#ADDED} for exactly one caller per (fileUuid, partNumber), all others get {@link PartResult#DUPLICATE}
     */
    PartResult registerPart(UUID fileUuid, int partNumber, long size);

    /**
     * @return true for exactly one caller (finished false -> true)
     */
    boolean markFileFinished(UUID fileUuid);

    /**
     * Compare-and-set of the batch status.
     *
     * @return true only if the status was {@code from} and is now {@code to}
     */
    boolean transition(UUID batchUuid, UploadBatchState from, UploadBatchState to);

    /**
     * Convenience: exactly one caller gets true and may run the on-complete handler.
     */
    default boolean tryClaimCompletion(UUID batchUuid)
    {
        return transition(batchUuid, UploadBatchState.OPEN, UploadBatchState.COMPLETING);
    }

    /**
     * Moves the expiry of an OPEN batch (call on every received part). @return false if batch is not OPEN
     */
    boolean touch(UUID batchUuid, Instant newExpiry);

    /**
     * Claims up to {@code limit} expired OPEN batches (OPEN -> EXPIRED). A batch is returned to exactly one caller, so exactly one instance
     * cleans it up. Not a read: it changes state.
     */
    List<UUID> claimExpired(Instant now, int limit);

    /**
     * Removes batch, files and parts.
     */
    void delete(UUID batchUuid);

}
