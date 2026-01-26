/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util.exception;

import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import lombok.Getter;

/**
 * Excpetion raised when a {@link InputExtractor} has failed input checks.
 * 
 * @since 3.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Getter
public final class InputExtractionException extends RuntimeException
{

    private final InputExtractor inputExtractor;

    public InputExtractionException(InputExtractor inEx)
    {
        super();

        this.inputExtractor = inEx;
    }

}
