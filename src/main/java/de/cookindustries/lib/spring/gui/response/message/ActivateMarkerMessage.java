/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response.message;

import de.cookindustries.lib.spring.gui.hmi.input.util.MarkerCategory;
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
    private final MarkerCategory category;

    @NonNull
    private final String         fieldId;

    @NonNull
    private final String         text;

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.MARKER;
    }

    public String getUid()
    {
        return String.format("input-icon-%s-%s-%s", formId, category.name().toLowerCase(), fieldId);
    }

}
