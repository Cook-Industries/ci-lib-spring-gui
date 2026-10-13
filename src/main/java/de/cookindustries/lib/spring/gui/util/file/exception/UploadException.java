/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file.exception;

import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.Status;

public class UploadException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    private final Status      status;

    public UploadException(Status status, String message)
    {
        super(message);

        this.status = status;
    }

    public UploadException(Status status, String message, Throwable cause)
    {
        super(message, cause);

        this.status = status;
    }

    public Status getStatus()
    {
        return status;
    }
}
