/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

public record InputParameters(String tooltip, String name, String submitAs, String onInput, String infoText, String infoUrl,
    boolean disabled, boolean mandatory)
{
}
