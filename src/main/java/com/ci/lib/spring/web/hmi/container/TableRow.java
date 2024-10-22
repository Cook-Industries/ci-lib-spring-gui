/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.ArrayList;

import lombok.Getter;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@Getter
public class TableRow
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
