/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.InputExtractionException;
import de.cookindustries.lib.spring.gui.hmi.util.GUIFactory;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;

/**
 * Controller advice to capture {@link InputExtractionException} and handle it gracefully.
 *
 * @since 3.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@ControllerAdvice
@ConditionalOnProperty(name = "cilib.input.checking.exception-output.enabled", havingValue = "true", matchIfMissing = true)
public final class CiLibInputExtractionExceptionHandler
{

    private final GUIFactory guiFactory;

    public CiLibInputExtractionExceptionHandler(GUIFactory guiFactory)
    {
        this.guiFactory = guiFactory;
    }

    @ExceptionHandler(InputExtractionException.class)
    public ResponseEntity<NotificationResponse> handleInputExtractionException(InputExtractionException ex)
    {
        InputExtractor       extractor = ex.getInputExtractor();
        NotificationResponse response  = guiFactory.createActiveMarkerResponseFrom(extractor);

        return ResponseEntity.ok(response);
    }

}
