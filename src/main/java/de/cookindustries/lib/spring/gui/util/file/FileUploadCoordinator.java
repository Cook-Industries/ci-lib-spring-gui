package de.cookindustries.lib.spring.gui.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore.BatchInfo;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore.FileInfo;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore.FileInit;
import de.cookindustries.lib.spring.gui.util.file.exception.FormCipherException;
import de.cookindustries.lib.spring.gui.util.file.exception.UploadException;

/**
 * Coordinates a chunked upload. The controller talks only to this class; it holds all "check, then act" logic and delegates
 * <ul>
 * <li>shared state to the {@link UploadSessionStore} (every decision rests on a return value of the store, never on an earlier read)</li>
 * <li>bytes to the {@link AbstractReceiver}</li>
 * <li>what happens with the finished upload to a {@link CompletionHandler}, found by the {@code completionHandlerKey} stored with the
 * batch</li>
 * </ul>
 * Flow: the form handler validates the form and calls {@link #startUpload}; the client then calls {@link #push} per part, {@link #finish}
 * per file and {@link #complete} once; {@link #abort} on failure; {@link #sweepExpired} cleans up what nobody finished.
 * <p>
 * The form POJO and the file descriptors (including file names) are stored together as one encrypted blob, so the store never sees a file
 * name and a file uuid cannot be traced back to one without decrypting.
 * <p>
 * Safe to run on several instances as long as the store is shared, all instances register the same handlers and the receiver writes to
 * storage all instances reach.
 */
public class FileUploadCoordinator
{

    public static final String  UPLOAD_BASE_URL_DEF = "${cook-industries.web.upload.url}";

    private static final Logger LOG                 = LoggerFactory.getLogger(FileUploadCoordinator.class);

    /**
     * Reason of a failed call, for mapping to http status codes in the controller.
     */
    public enum Status
    {
        BAD_REQUEST, NOT_FOUND, CONFLICT, TOO_LARGE, FAILED, INTERNAl_ERROR
    }

    /**
     * A file as the client announces it.
     */
    public record FileDeclaration(String fieldId, String filename, long length)
    {
    }

    /**
     * Result of {@link #startUpload}; the form handler maps it to its response.
     */
    public record StartResult(UUID batchUuid, UploadSessionPolicy policy, List<UploadSessionState> files)
    {
    }

    private final UploadSessionStore                   store;
    private final AbstractReceiver                     receiver;
    private final AbstractFormCipher                   cipher;
    private final UploadSessionPolicy                  policy;
    private final UploadSessionLimits                  limits;
    private final Map<String, CompletionHandler<?, ?>> handlers = new HashMap<>();

    public FileUploadCoordinator(UploadSessionStore store, AbstractReceiver receiver, AbstractFormCipher cipher,
        List<CompletionHandler<?, ?>> handlers, UploadSessionPolicy policy, UploadSessionLimits limits)
    {
        this.store = Objects.requireNonNull(store, "store");
        this.receiver = Objects.requireNonNull(receiver, "receiver");
        this.cipher = Objects.requireNonNull(cipher, "cipher");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.limits = Objects.requireNonNull(limits, "limits");

        for (CompletionHandler<?, ?> handler : Objects.requireNonNull(handlers, "handlers"))
        {
            if (this.handlers.putIfAbsent(handler.completeKey(), handler) != null)
            {
                throw new IllegalStateException("duplicate completeKey: " + handler.completeKey());
            }
        }
    }

    /**
     * Creates a batch for a validated form. Call this from the form handler after the input check passed.
     * 
     * @param completionHandlerKey key of the {@link CompletionHandler} that takes over when the upload is complete
     * @param formDataSupplier the validated form data; must be an instance of the handler's form type
     * @param declared the files the client announced
     */
    public StartResult startUpload(String completionHandlerKey, Supplier<?> formDataSupplier, List<FileDeclaration> declared)
    {
        LOG.debug("file upload started");

        CompletionHandler<?, ?> handler = handlers.get(completionHandlerKey);

        if (handler == null)
        {
            throw new IllegalArgumentException("no completion handler registered for key: " + completionHandlerKey);
        }

        if (!handler.formType().isInstance(formDataSupplier.get()))
        {
            throw new IllegalArgumentException("form is not a " + handler.formType().getName());
        }

        if (declared == null || declared.isEmpty())
        {
            throw new UploadException(Status.BAD_REQUEST, "no files announced");
        }

        long total = 0;

        for (FileDeclaration d : declared)
        {
            if (d.fieldId() == null
                || d.fieldId().isBlank()
                || d.filename() == null
                || d.filename().isBlank())
            {
                throw new UploadException(Status.BAD_REQUEST, "fieldId and filename are required");
            }

            if (d.length() < 0)
            {
                throw new UploadException(Status.BAD_REQUEST, "negative length for " + d.filename());
            }

            if (d.length() > limits.maxFileLength())
            {
                throw new UploadException(Status.TOO_LARGE, "file exceeds " + limits.maxFileLength() + " bytes: " + d.filename());
            }

            total += d.length();

            if (total > limits.maxBatchLength())
            {
                throw new UploadException(Status.TOO_LARGE, "upload exceeds " + limits.maxBatchLength() + " bytes");
            }
        }

        UUID                     batchUuid = UUID.randomUUID();
        Instant                  now       = Instant.now();

        List<FileInit>           inits     = new ArrayList<>();
        List<UploadedFile>       uploaded  = new ArrayList<>();
        List<UploadSessionState> states    = new ArrayList<>();

        LOG.debug("assigned batch uuid [{}] expecting [{}] files", batchUuid, declared.size());

        for (FileDeclaration d : declared)
        {
            UUID fileUuid = UUID.randomUUID();

            inits.add(new FileInit(fileUuid, d.fieldId(), d.length()));
            uploaded.add(new UploadedFile(fileUuid, d.fieldId(), d.filename(), d.length()));

            states
                .add(
                    UploadSessionState
                        .builder()
                        .uuid(fileUuid)
                        .batchUuid(batchUuid)
                        .fieldId(d.fieldId())
                        .filename(d.filename())
                        .created(now)
                        .expectedLength(d.length())
                        .build());
        }

        SealedForm sealed;

        try
        {
            sealed = cipher.seal(batchUuid, completionHandlerKey, formDataSupplier, uploaded);
        }
        catch (FormCipherException ex)
        {
            throw new UploadException(Status.FAILED, "form could not be sealed", ex);
        }

        store
            .createBatch(
                new BatchInfo(batchUuid, UploadBatchState.OPEN, policy.getPartLength(), completionHandlerKey, sealed.keyId(), sealed.data(),
                    now.plus(limits.sessionTtl())),
                inits);

        return new StartResult(batchUuid, policy, states);
    }

    /**
     * Receives one part. Safe to repeat: a part that is already registered is accepted again.
     *
     * @param length number of bytes announced by the client (content length)
     */
    public FileUploadResponse push(UUID batchUuid, UUID fileUuid, int partNumber, long length, InputStream in)
    {
        LOG.debug("push file [{}] part [{}] belonging to batch [{}]", fileUuid, partNumber, batchUuid);

        BatchInfo batch = requireOpenBatch(batchUuid);
        FileInfo  file  = requireFile(batchUuid, fileUuid);

        if (file.finished())
        {
            throw new UploadException(Status.CONFLICT, "file is already finished");
        }

        if (length != expectedPartLength(file.expectedLength(), batch.partLength(), partNumber))
        {
            throw new UploadException(Status.BAD_REQUEST, "wrong length for part " + partNumber);
        }

        long offset = (long) (partNumber - 1) * batch.partLength();

        // bytes first, registration second: if the write fails the part stays unregistered and a retry is not seen as duplicate
        try
        {
            receiver.receive(fileUuid, partNumber, offset, length, in);
        }
        catch (IOException e)
        {
            throw new UploadException(Status.FAILED, "part " + partNumber + " could not be received", e);
        }

        store.registerPart(fileUuid, partNumber, length);

        if (!store.touch(batchUuid, Instant.now().plus(limits.sessionTtl())))
        {
            Optional<BatchInfo> now = store.findBatch(batchUuid);

            if (now.isEmpty() || now.get().status() == UploadBatchState.ABORTED || now.get().status() == UploadBatchState.EXPIRED)
            {
                abortQuietly(fileUuid);
            }

            throw new UploadException(Status.CONFLICT, "upload is no longer open");
        }

        LOG.debug("received file [{}] part [{}] belonging to batch [{}]", fileUuid, partNumber, batchUuid);

        return new FileUploadResponse("part received");
    }

    /**
     * Closes one file after all its parts are in. Safe to repeat.
     */
    public FileUploadResponse finish(UUID batchUuid, UUID fileUuid)
    {
        LOG.debug("finish uploading file [{}] belonging to batch [{}]", fileUuid, batchUuid);

        requireOpenBatch(batchUuid);

        FileInfo file = requireFile(batchUuid, fileUuid);

        if (file.submittedLength() != file.expectedLength())
        {
            throw new UploadException(Status.CONFLICT,
                "file incomplete: " + file.submittedLength() + " of " + file.expectedLength() + " bytes");
        }

        try
        {
            receiver.finish(fileUuid);
        }
        catch (IOException e)
        {
            throw new UploadException(Status.FAILED, "file could not be finished", e);
        }

        // the flag is set only after the data is final
        store.markFileFinished(fileUuid);

        return new FileUploadResponse("file upload finished");
    }

    /**
     * Hands the finished upload to its completion handler, exactly once even if several calls or instances race.
     *
     * @return the result of the handler
     */
    public Object complete(UUID batchUuid)
    {
        LOG.debug("complete batch [{}]", batchUuid);

        BatchInfo      batch = requireOpenBatch(batchUuid);
        List<FileInfo> files = store.findByBatch(batchUuid);

        for (FileInfo file : files)
        {
            if (!file.finished())
            {
                throw new UploadException(Status.CONFLICT, "not all files are finished");
            }
        }

        CompletionHandler<?, ?> handler = handlers.get(batch.completeKey());

        if (handler == null)
        {
            throw new UploadException(Status.FAILED, "no completion handler registered on this instance for: " + batch.completeKey());
        }

        if (!store.tryClaimCompletion(batchUuid))
        {
            throw new UploadException(Status.CONFLICT, "upload is already being completed");
        }

        Object result;

        try
        {
            result = invoke(handler, batch);
        }
        catch (Exception e)
        {
            LOG.error("completion of upload {} failed, discarding it", batchUuid, e);

            store.transition(batchUuid, UploadBatchState.COMPLETING, UploadBatchState.ABORTED);
            cleanup(batchUuid);

            throw new UploadException(Status.FAILED, "completion failed", e);
        }

        store.transition(batchUuid, UploadBatchState.COMPLETING, UploadBatchState.DONE);
        store.delete(batchUuid);

        return result;
    }

    /**
     * Discards a batch and everything received for it. Safe to repeat. Refused once completion has started.
     */
    public FileUploadResponse abort(UUID batchUuid)
    {
        LOG.debug("abort batch [{}]", batchUuid);

        if (!store.transition(batchUuid, UploadBatchState.OPEN, UploadBatchState.ABORTED))
        {
            Optional<BatchInfo> current = store.findBatch(batchUuid);

            if (current.isEmpty())
            {
                return fileAbortedResponse();
            }

            UploadBatchState state = current.get().status();

            if (state == UploadBatchState.COMPLETING || state == UploadBatchState.DONE)
            {
                throw new UploadException(Status.CONFLICT, "upload is already being completed");
            }

            // ABORTED or EXPIRED: somebody else claimed it, cleanup below is idempotent
        }

        cleanup(batchUuid);

        return fileAbortedResponse();
    }

    private final FileUploadResponse fileAbortedResponse()
    {
        return new FileUploadResponse("file aborted");
    }

    /**
     * Cleans up batches that nobody finished in time. Call this periodically from any scheduler; every instance may call it.
     *
     * @return number of batches removed by this call
     */
    public int sweepExpired(int limit)
    {
        List<UUID> expired = store.claimExpired(Instant.now(), limit);

        for (UUID batchUuid : expired)
        {
            cleanup(batchUuid);
        }

        return expired.size();
    }

    private <F, T extends Response> T invoke(CompletionHandler<F, T> handler, BatchInfo batch) throws Exception
    {
        AbstractFormCipher.OpenedForm<F> opened =
            cipher.open(batch.batchUuid(), batch.completeKey(), batch.keyId(), batch.encryptedFormPojo(), handler.formType());

        return handler.onComplete(opened.form(), opened.files());
    }

    private BatchInfo requireOpenBatch(UUID batchUuid)
    {
        BatchInfo batch =
            store
                .findBatch(batchUuid)
                .orElseThrow(() -> new UploadException(Status.NOT_FOUND, "unknown upload"));

        if (batch.status() != UploadBatchState.OPEN)
        {
            throw new UploadException(Status.CONFLICT, "upload is " + batch.status());
        }

        return batch;
    }

    /**
     * The file must belong to the given batch, otherwise a valid file uuid of another upload could be used here.
     */
    private FileInfo requireFile(UUID batchUuid, UUID fileUuid)
    {
        return store
            .findFile(fileUuid)
            .filter(f -> f.batchUuid().equals(batchUuid))
            .orElseThrow(() -> new UploadException(Status.NOT_FOUND, "unknown file"));
    }

    private static long expectedPartLength(long fileLength, long partLength, int partNumber)
    {
        long parts = (fileLength + partLength - 1) / partLength;

        if (partNumber < 1 || partNumber > parts)
        {
            throw new UploadException(Status.BAD_REQUEST, "part number out of range: " + partNumber);
        }

        return partNumber < parts ? partLength : fileLength - (parts - 1) * (long) partLength;
    }

    /**
     * Drops received data and store entries of a batch that is already claimed (ABORTED, EXPIRED or failed). A receiver that cannot abort
     * is only logged, so a store entry never outlives its claim.
     */
    private void cleanup(UUID batchUuid)
    {
        for (FileInfo file : store.findByBatch(batchUuid))
        {
            abortQuietly(file.fileUuid());
        }

        store.delete(batchUuid);
    }

    private void abortQuietly(UUID fileUuid)
    {
        try
        {
            receiver.abort(fileUuid);
        }
        catch (IOException | RuntimeException e)
        {
            LOG.warn("receiver could not abort file {}", fileUuid, e);
        }
    }
}
