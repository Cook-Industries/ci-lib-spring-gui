/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class TagList extends ArrayList<Tag>
{

    @Override
    public String toString()
    {
        return this
            .stream()
            .map(Tag::getValue)
            .collect(Collectors.joining(", "));
    }
}
