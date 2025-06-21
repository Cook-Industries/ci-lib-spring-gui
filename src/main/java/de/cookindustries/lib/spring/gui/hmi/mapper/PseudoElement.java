/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import java.util.List;
import java.util.Map;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ContainerType;
import de.cookindustries.lib.spring.gui.hmi.input.InputType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@code PseudoElemtent} describes a parsed element from a JSON file, that gets interpreted by a {@link JsonMapper} to transform the
 * elements to {@link Container}s.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@Jacksonized
public class PseudoElement
{

    /**
     * Unique id of this element
     */
    @Default
    private final String              uid = null;

    /**
     * Type of this element based on {@link ContainerType}, {@link InputType} or {@link InternalElementType}
     */
    @NonNull
    private final String              type;

    /**
     * Class modifiers to apply to this
     */
    @Singular
    private final List<String>        classes;

    /**
     * Attributes of this element
     */
    @Singular
    private final Map<String, String> attributes;

    /**
     * Parameters of this element
     */
    private final Map<String, String> parameters;

    /**
     * Marker for interactive messaging
     */
    @Singular("mark")
    private final List<PseudoMarker>  marker;

    /**
     * Children of this element
     */
    @Singular
    private final List<PseudoElement> children;

}
