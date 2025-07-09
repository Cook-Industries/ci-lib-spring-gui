/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Singular;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Getter
public final class TableRow
{

    @Singular
    private final ArrayList<Object> fields;

    public TableRow()
    {
        fields = new ArrayList<>();
    }

    public TableRow addField(Object o)
    {
        fields.add(o);
        return this;
    }

}
