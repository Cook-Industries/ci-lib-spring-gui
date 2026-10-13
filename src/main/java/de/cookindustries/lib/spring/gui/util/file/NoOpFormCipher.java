/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cipher that does <b>not</b> encrypt. The form is stored as plain serialized JSON.
 * <p>
 * Only suitable where the store is trusted and not readable by third parties, e.g. the in-memory store of a single instance. With a shared
 * database this puts the submitted form data, including all file names, into the tables in clear text, and nothing protects the blob
 * against being changed or moved to another batch. Use a real {@link AbstractFormCipher} implementation there.
 * <p>
 * Uses no key ids; the id is always the empty string.
 */
public final class NoOpFormCipher extends AbstractFormCipher
{

    private static final Logger LOG = LoggerFactory.getLogger(NoOpFormCipher.class);

    public NoOpFormCipher()
    {
        LOG.warn("NoOpFormCipher is active: upload form data is stored unencrypted");
    }

    @Override
    protected SealedForm encrypt(UUID batchUuid, String handlerKey, byte[] plain)
    {
        // the caller clears its buffer afterwards, so the result must be a copy
        return new SealedForm("", plain.clone());
    }

    @Override
    protected byte[] decrypt(UUID batchUuid, String handlerKey, String keyId, byte[] data)
    {
        // the caller clears the result afterwards, so it must not be the array the store handed out
        return data.clone();
    }
}
