/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import org.apache.commons.text.StringEscapeUtils;

import de.cookindustries.lib.spring.gui.html.HtmlExportable;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

/**
 * Describes a HTML element attribute. A {@code null} {@code Attribute#value} results in a empty html attribute, like 'controls'.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public final class Attribute implements HtmlExportable
{

    /** name of the attribute */
    @NonNull
    private final String  name;

    /** value of the attribute */
    @Default
    private final String  value  = null;

    /** whether this attribut should be active */
    @Default
    private final boolean active = true;

    /**
     * Render this {@code Attribute}.
     * <p>
     * A {@code false} {@code Attribute#active} results in an empty {@code String}.
     * <p>
     * A {@code null} in {@code Attribute#value} results in a {@code Attribute#name} only {@code Attribute}, like 'checked'.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String getHtmlRep()
    {
        if (!active)
        {
            return "";
        }

        return value == null
            ? StringAdapter.from(name, "=\"", name, "\"")
            : StringAdapter.from(name, "=\"", StringEscapeUtils.escapeHtml4(value), "\"");
    }
}
