/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.response.message.ResponseMessage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class Response
{

    /**
     * the action associated with this response
     */
    @NonNull
    private final ResponseAction  action = inferType();

    /**
     * a list of {@link ResponseMessage}s to show in the GUI
     */
    @Singular
    private List<ResponseMessage> messages;

    /**
     * a list of {@link AbsFunctionCall}s to perform on the GUI
     */
    @Singular
    private List<AbsFunctionCall> calls;

    /**
     * Infer the concrete type of action for the implementing class
     * 
     * @return the action this class represents
     */
    protected abstract ResponseAction inferType();

}
