/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

/**
 * Functional Interface for type bound resolve inside {@link JsonMapper}.
 * 
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@FunctionalInterface
interface TypeResolver<T, R>
{

    R apply(JsonMapper mapper, T type, PseudoElement element, int depth);
}
