/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import com.ci.lib.spring.web.util.StringAdapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Attribute
{

    private final String name;
    private final String value;
    private Boolean      show      = true;

    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private Boolean      valueless = false;

    /**
     * Create a valueless html attribute. E.g. 'checked';
     */
    public Attribute(String name, Boolean show)
    {
        this(name, "", show, true);
    }

    public String html()
    {
        String result = valueless ? name : StringAdapter.from(name, "=\"", value, "\"");

        return show ? result : "";
    }
}
