/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.ArrayList;

import lombok.Getter;

/**
 * 
 */
@Getter
public final class TableRow
{

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
