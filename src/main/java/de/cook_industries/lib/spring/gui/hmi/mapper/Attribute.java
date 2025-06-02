/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import de.cook_industries.lib.spring.gui.html.HtmlExportable;
import de.cook_industries.lib.spring.gui.util.StringAdapter;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlRep()
    {
        return valueless ? name : StringAdapter.from(name, "=\"", value, "\"");
    }
}
