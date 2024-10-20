/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import com.ci.lib.spring.web.html.HtmlExportable;
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
public class Attribute implements HtmlExportable
{

    private final String name;
    private final String value;
    private Boolean      show      = true;

    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private Boolean      valueless = false;

    /**
     * Create a valueless html attribute. E.g. 'checked';
     *
     * @param name of this {@code Attribute}
     * @param show wether this arrtibute is active or not
     */
    public Attribute(String name, Boolean show)
    {
        this(name, "", show, true);
    }

    @Override
    public String getHtmlRep()
    {
        String result = valueless ? name : StringAdapter.from(name, "=\"", value, "\"");

        return show ? result : "";
    }
}
