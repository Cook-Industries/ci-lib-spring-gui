/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response.message;

import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerCategory;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ActivateMarkerMessage extends ResponseMessage
{

    @NonNull
    private final String         formId;

    @NonNull
    private final String         transferId;

    @NonNull
    private final MarkerCategory markerCategory;

    @NonNull
    private final MarkerType     markerType;

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.MARKER;
    }

}
