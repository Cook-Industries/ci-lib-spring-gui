/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

/**
 * This interface describes a {@code POJO} that can be mapped to key-value pairs in a {@link Map}.
 * <p>
 * Objects of this type should only have plain variable types, no list or array structures. This limitation is based in the fact that a
 * transfomation in the {@link FlatMappableDissector} cannot resolve {@code List} or {@code Array} to a viable flattened structure, that is
 * easily interated by direct indicies.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public interface FlatMappable
{
    // no functionallity since this is only a type interface
}
