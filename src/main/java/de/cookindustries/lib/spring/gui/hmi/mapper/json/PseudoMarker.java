/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@code PseudoMarker} describes a parsed element from a JSON file, that gets interpreted by a {@link JsonMapper} to transform the
 * elements to {@link Marker}s.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class PseudoMarker
{

    @NonNull
    private final String category;

    @NonNull
    private final String type;

    @NonNull
    private final String text;

}
