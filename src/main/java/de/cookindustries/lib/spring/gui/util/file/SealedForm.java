/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

/**
 * Encrypted form blob together with the id of the key that produced it.
 *
 * @param keyId opaque to everybody except the {@link AbstractFormCipher} implementation: handed back unchanged on decryption. May be any
 *            non-null string, also an empty one for implementations without key ids.
 * @param data the ciphertext
 */
public record SealedForm(String keyId, byte[] data)
{
}
