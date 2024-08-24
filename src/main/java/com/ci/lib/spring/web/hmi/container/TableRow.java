/**
 * Copyright(c) 2021 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 10.11.2021 Author : sebastian koch <koch.sebastian@cook-industries.de>
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
