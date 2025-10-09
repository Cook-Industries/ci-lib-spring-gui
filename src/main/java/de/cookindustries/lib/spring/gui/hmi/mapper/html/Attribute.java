/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import de.cookindustries.lib.spring.gui.html.HtmlExportable;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;

/**
 * Describes a HTML element attribute
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

    /** whether this attribut should be active (inactive results in an empty result) */
    @Default
    @NonNull
    private final Boolean active = true;

    /**
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
            : StringAdapter.from(name, "=\"", value, "\"");
    }
}
