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
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
@ToString
public final class SearchTagsResponse extends Response
{

    @NonNull
    private final String       inputId;

    @NonNull
    private final String       originalInputValue;

    @Singular
    private final List<String> results;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.FETCH_TAGS_RESULT;
    }

}
