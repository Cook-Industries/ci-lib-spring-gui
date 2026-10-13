/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.util.file.exception.FormCipherException;

/**
 * Encrypts the stored form blob. The implementation owns keys and key ids.
 */
public abstract class AbstractFormCipher
{

    /**
     * Result of {@link AbstractFormCipher#open}.
     */
    public record OpenedForm<F>(F form, List<UploadedFile> files)
    {
    }

    /**
     * What is encrypted: the form as a tree (its class is only known to the handler) and the file descriptors.
     */
    record Envelope(JsonNode form, List<UploadedFile> files)
    {
    }

    private final ObjectMapper mapper;

    /**
     * Uses a mapper owned by this class, with all modules found on the classpath (java.time and others).
     */
    protected AbstractFormCipher()
    {
        this(new ObjectMapper().findAndRegisterModules());
    }

    /**
     * Uses a copy of the given mapper, for forms that need special handling.
     */
    protected AbstractFormCipher(ObjectMapper mapper)
    {
        Objects.requireNonNull(mapper, "mapper");

        this.mapper =
            mapper
                .copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.mapper.deactivateDefaultTyping();
    }

    /**
     * Serializes and encrypts form and files.
     */
    public final SealedForm seal(UUID batchUuid, String handlerKey, Object form, List<UploadedFile> files)
    {
        Objects.requireNonNull(batchUuid, "batchUuid");
        Objects.requireNonNull(handlerKey, "handlerKey");
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(files, "files");

        byte[] plain;

        try
        {
            plain = mapper.writeValueAsBytes(new Envelope(mapper.valueToTree(form), List.copyOf(files)));
        }
        catch (JsonProcessingException | IllegalArgumentException e)
        {
            throw new FormCipherException("form could not be serialized", e);
        }

        try
        {
            SealedForm sealed = encrypt(batchUuid, handlerKey, plain);

            if (sealed == null || sealed.keyId() == null || sealed.data() == null)
            {
                throw new FormCipherException("encrypt returned an incomplete result");
            }

            return sealed;
        }
        finally
        {
            Arrays.fill(plain, (byte) 0);
        }
    }

    /**
     * Decrypts and deserializes into the given form type.
     *
     * @param formType the form class of the handler that completes the upload
     */
    public final <F> OpenedForm<F> open(UUID batchUuid, String handlerKey, String keyId, byte[] data, Class<F> formType)
    {
        Objects.requireNonNull(batchUuid, "batchUuid");
        Objects.requireNonNull(handlerKey, "handlerKey");
        Objects.requireNonNull(keyId, "keyId");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(formType, "formType");

        byte[] plain = decrypt(batchUuid, handlerKey, keyId, data);

        if (plain == null)
        {
            throw new FormCipherException("decrypt returned nothing");
        }

        try
        {
            Envelope           envelope = mapper.readValue(plain, Envelope.class);
            List<UploadedFile> files    = envelope.files() == null ? List.of() : envelope.files();

            return new OpenedForm<>(mapper.treeToValue(envelope.form(), formType), files);
        }
        catch (IOException e)
        {
            throw new FormCipherException("form could not be read", e);
        }
        finally
        {
            Arrays.fill(plain, (byte) 0);
        }
    }

    /**
     * Encrypts with the currently active key.
     *
     * @param batchUuid bind as authenticated data
     * @param handlerKey bind as authenticated data
     * @param plain serialized form; cleared by the caller afterwards
     */
    protected abstract SealedForm encrypt(UUID batchUuid, String handlerKey, byte[] plain);

    /**
     * Decrypts with the key named by {@code keyId}; must verify the authenticated data.
     *
     * @return the plain bytes; cleared by the caller afterwards
     */
    protected abstract byte[] decrypt(UUID batchUuid, String handlerKey, String keyId, byte[] data);
}
