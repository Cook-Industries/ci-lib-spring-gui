/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.Call;
import de.cookindustries.lib.spring.gui.response.message.ResponseMessage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class Response
{

    @NonNull
    private final Action          action = inferType();

    @Singular
    private List<ResponseMessage> messages;

    @Singular
    private List<Call>            calls;

    protected abstract Action inferType();

}
