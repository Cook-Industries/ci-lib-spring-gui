/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file.exception;

/**
 * Failure of sealing or opening, unchecked.
 */
public final class FormCipherException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public FormCipherException(String message)
    {
        super(message);
    }

    public FormCipherException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
