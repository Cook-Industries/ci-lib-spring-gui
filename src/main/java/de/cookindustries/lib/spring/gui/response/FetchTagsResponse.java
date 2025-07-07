/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
public class FetchTagsResponse extends Response
{

    @NonNull
    private final String       inputId;

    @Singular
    private final List<String> results;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.FETCH_TAGS;
    }

}
