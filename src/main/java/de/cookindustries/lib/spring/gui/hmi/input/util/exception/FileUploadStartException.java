package de.cookindustries.lib.spring.gui.hmi.input.util.exception;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.StartResult;

/**
 * Signals that a validated form announced files and an upload was started for it. It ends the form handler at the point of
 * {@link InputExtractor#approve(String, java.util.function.Supplier)}, the same way a failed validation does with
 * {@link InputExtractionException}.
 * <p>
 * It is meant to be captured by an exception handler, which turns {@link #getResult()} into the {@code StartFileUploadResponse} for the
 * client. The rest of the form is processed later by the completion handler, once all files are uploaded.
 * <p>
 * Carries no stack trace, since it is used for control flow.
 *
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class FileUploadStartException
    extends RuntimeException
{

    private static final long           serialVersionUID = 1L;

    private final transient StartResult result;

    private final String                completionHandlerKey;

    public FileUploadStartException(StartResult result, String completionHandlerKey)
    {
        super("file upload started", null, false, false);

        this.result = result;
        this.completionHandlerKey = completionHandlerKey;
    }

    /**
     * Get the upload that was started.
     *
     * @return batch, policy and file descriptors for the client
     */
    public StartResult getResult()
    {
        return result;
    }

    /**
     * Get the key of the completion handler that takes over after the upload.
     *
     * @return the key the form was registered with
     */
    public String getCompletionHandlerKey()
    {
        return completionHandlerKey;
    }
}
