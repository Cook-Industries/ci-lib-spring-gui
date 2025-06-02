/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cook_industries.lib.spring.gui.hmi.container.Container;
import de.cook_industries.lib.spring.gui.hmi.container.ContainerType;
import de.cook_industries.lib.spring.gui.hmi.input.InputType;
import de.cook_industries.lib.spring.gui.hmi.input.util.Marker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A {@code PseudoElemtent} describes a parsed element from a JSON file, that gets interpreted by a {@link JsonMapper} to transform the
 * elements to {@link Container}s.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PseudoElement
{

    /**
     * Unique id of this element
     */
    private String              uid        = null;

    /**
     * Type of this element based on {@link ContainerType}, {@link InputType} or {@link MapperInternalElementType}
     */
    private String              type;

    /**
     * Class modifiers to apply to this
     */
    private List<String>        classes    = new ArrayList<>();

    /**
     * Attributes of this element
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Parameters of this element
     */
    private Map<String, String> parameters = new HashMap<>();

    /**
     * Marker for interactive messaging
     */
    private List<Marker>        marker     = new ArrayList<>();

    /**
     * Children of this element
     */
    private List<PseudoElement> children   = new ArrayList<>();

}
