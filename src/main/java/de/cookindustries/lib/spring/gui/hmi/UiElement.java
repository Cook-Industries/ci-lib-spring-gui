/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.micrometer.common.lang.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Singular;

/**
 * Base Class for all UI elements that have a unique id, styling classes and attributes
 */
@SuperBuilder
@Getter
public abstract class UiElement
{

    /**
     * the unique id for this element
     */
    @NonNull
    @Default
    private String              uid = UUID.randomUUID().toString();

    /**
     * a list of styling classes
     */
    @Singular("clazz")
    private Set<String>         classes;

    /**
     * a map of attributes for this element
     */
    @Singular
    private Map<String, String> dataAttributes;

}
