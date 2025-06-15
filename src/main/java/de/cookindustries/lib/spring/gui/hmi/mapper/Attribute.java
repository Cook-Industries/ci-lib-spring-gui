/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import de.cookindustries.lib.spring.gui.html.HtmlExportable;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Describes a HTML element attribute
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Attribute implements HtmlExportable
{

    /**
     * name of the attribute
     */
    private final String name;

    /**
     * value of the attribute
     */
    private final String value;

    /**
     * whether this attribute has a value or is name only, e.g. 'checked'
     */
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private Boolean      valueless = false;

    /**
     * if this attribute is valueless, it can be active or not
     */
    private Boolean      active    = true;

    /**
     * Create a valueless html attribute. E.g. 'checked';
     *
     * @param name of this {@code Attribute}
     * @param active wether this attribute is active or not
     */
    public Attribute(String name, Boolean active)
    {
        this(name, "", active, true);
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
