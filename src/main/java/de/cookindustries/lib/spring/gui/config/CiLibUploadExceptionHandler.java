/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.cookindustries.lib.spring.gui.util.file.FileUploadResponse;
import de.cookindustries.lib.spring.gui.util.file.exception.UploadException;

/**
 * Controller advice to capture {@link UploadException} and handle it gracefully.
 *
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@ControllerAdvice
public final class CiLibUploadExceptionHandler
{

    public CiLibUploadExceptionHandler()
    {}

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<FileUploadResponse> handleInputExtractionException(UploadException ex)
    {
        HttpStatus status = switch (ex.getStatus())
        {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case TOO_LARGE -> HttpStatus.PAYLOAD_TOO_LARGE;
            case FAILED, INTERNAl_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity
            .status(status)
            .body(new FileUploadResponse(ex.getMessage()));
    }

}
