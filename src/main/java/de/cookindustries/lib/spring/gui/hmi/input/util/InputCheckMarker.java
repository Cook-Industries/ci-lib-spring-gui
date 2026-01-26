/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class InputCheckMarker
{

    @NonNull
    private final String         formId;

    @NonNull
    private final String         transferId;

    @NonNull
    private final MarkerCategory category;

    @NonNull
    private final MarkerType     type;

}
