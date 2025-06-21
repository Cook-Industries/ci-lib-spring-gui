/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.marker;

import de.cookindustries.lib.spring.gui.hmi.input.Input;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

/**
 * This object represents information elements on some {@link Input} fields, to highlight bad user input, show notes or warnings.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@Jacksonized
public class Marker
{

    /**
     * the {@code submitAs} value of the input to which this {@code marker} belongs
     */
    @NonNull
    private final String         ownerId;

    /**
     * the category of this {@code marker}
     */
    @NonNull
    private final MarkerCategory category;

    /**
     * the type of state the value is in to show this {@code marker}
     */
    @NonNull
    private final MarkerType     type;

    /**
     * the text describing the information for the user
     */
    @NonNull
    private final String         text;

}
