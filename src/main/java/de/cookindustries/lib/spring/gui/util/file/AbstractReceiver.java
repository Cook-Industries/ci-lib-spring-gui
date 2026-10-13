/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * Turns the bytes of an upload into stored data. Knows nothing about batches, stores, policies or forms: it gets a stream and has to handle
 * it.
 * <p>
 * The public methods are final and guard the contract; implementations only fill in {@link #doReceive}, {@link #doFinish} and
 * {@link #doAbort}.
 * <p>
 * <b>Contract for implementations</b>
 * <ul>
 * <li>Thread-safe: parts of the same file arrive in parallel, in any order and possibly on different instances. Write by {@code offset},
 * never assume sequential arrival.</li>
 * <li>{@code doReceive} must be idempotent per part: the same part may arrive again after a client retry and must produce identical data at
 * the same position.</li>
 * <li>{@code doFinish} and {@code doAbort} must be idempotent and {@code doAbort} must also work for a file that never received
 * anything.</li>
 * <li>Throwing means failure. The coordinator then answers with an error and the client retries.</li>
 * <li>A multi-instance setup needs storage that all instances reach (shared storage, S3 multipart, ...), a local temp file only works for
 * single instance.</li>
 * </ul>
 * <b>Safeguards enforced here</b>
 * <ul>
 * <li>The implementation can never read more than {@code length} bytes from the stream.</li>
 * <li>A stream that ends before {@code length} bytes throws an {@link EOFException} into the implementation.</li>
 * <li>An implementation that does not consume exactly {@code length} bytes fails the part.</li>
 * <li>A request body that carries more than {@code length} bytes fails the part.</li>
 * <li>The caller's stream is never closed by the implementation.</li>
 * </ul>
 */
public abstract class AbstractReceiver
{

    /**
     * Receives one part.
     *
     * @param fileUuid target file
     * @param partNumber number of the part, starting at 1
     * @param offset byte position of this part inside the file
     * @param length exact number of bytes of this part
     * @param in stream carrying the part
     * @throws IOException if the part could not be received completely
     */
    public final void receive(UUID fileUuid, int partNumber, long offset, long length, InputStream in) throws IOException
    {
        Objects.requireNonNull(fileUuid, "fileUuid");
        Objects.requireNonNull(in, "in");

        if (partNumber < 1)
        {
            throw new IllegalArgumentException("partNumber must be >= 1: " + partNumber);
        }

        if (offset < 0)
        {
            throw new IllegalArgumentException("offset must be >= 0: " + offset);
        }

        if (length < 0)
        {
            throw new IllegalArgumentException("length must be >= 0: " + length);
        }

        ExactLengthInputStream bounded = new ExactLengthInputStream(in, length);

        doReceive(fileUuid, partNumber, offset, length, bounded);

        if (bounded.consumed() != length)
        {
            throw new IOException(
                "receiver consumed " + bounded.consumed() + " of " + length + " bytes (file " + fileUuid + ", part " + partNumber + ")");
        }

        if (in.read() != -1)
        {
            throw new IOException(
                "part carries more than the announced " + length + " bytes (file " + fileUuid + ", part " + partNumber + ")");
        }
    }

    /**
     * Called once all parts of a file are in. May be called more than once for the same file.
     */
    public final void finish(UUID fileUuid) throws IOException
    {
        Objects.requireNonNull(fileUuid, "fileUuid");

        doFinish(fileUuid);
    }

    /**
     * Discards everything that exists for the file. May be called more than once and for files that never received data.
     */
    public final void abort(UUID fileUuid) throws IOException
    {
        Objects.requireNonNull(fileUuid, "fileUuid");

        doAbort(fileUuid);
    }

    protected abstract void doReceive(UUID fileUuid, int partNumber, long offset, long length, InputStream in) throws IOException;

    protected abstract void doFinish(UUID fileUuid) throws IOException;

    protected abstract void doAbort(UUID fileUuid) throws IOException;

    /**
     * Hands out at most {@code limit} bytes, fails on a premature end and ignores close.
     */
    private static final class ExactLengthInputStream
        extends FilterInputStream
    {

        private final long limit;

        private long       consumed = 0;

        ExactLengthInputStream(InputStream in, long limit)
        {
            super(in);

            this.limit = limit;
        }

        long consumed()
        {
            return consumed;
        }

        @Override
        public int read() throws IOException
        {
            if (consumed >= limit)
            {
                return -1;
            }

            int b = in.read();

            if (b == -1)
            {
                throw new EOFException("stream ended after " + consumed + " of " + limit + " bytes");
            }

            consumed++;

            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            if (len == 0)
            {
                return 0;
            }

            long remaining = limit - consumed;

            if (remaining <= 0)
            {
                return -1;
            }

            int n = in.read(b, off, (int) Math.min(len, remaining));

            if (n == -1)
            {
                throw new EOFException("stream ended after " + consumed + " of " + limit + " bytes");
            }

            consumed += n;

            return n;
        }

        @Override
        public long skip(long n) throws IOException
        {
            if (n <= 0)
            {
                return 0;
            }

            long skipped = in.skip(Math.min(n, limit - consumed));

            consumed += skipped;

            return skipped;
        }

        @Override
        public int available() throws IOException
        {
            return (int) Math.min(in.available(), limit - consumed);
        }

        @Override
        public boolean markSupported()
        {
            return false;
        }

        @Override
        public synchronized void mark(int readlimit)
        {
            // not supported
        }

        @Override
        public synchronized void reset() throws IOException
        {
            throw new IOException("mark/reset not supported");
        }

        @Override
        public void close()
        {
            // the caller owns the underlying stream
        }
    }
}
