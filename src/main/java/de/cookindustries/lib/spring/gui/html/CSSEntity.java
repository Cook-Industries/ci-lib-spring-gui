/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public class CSSEntity implements HtmlExportable
{

    @NonNull
    private final String             name;

    @NonNull
    private final CSSEntityType      type;

    @Singular
    private final List<CSSAttribute> attributes;

    @Override
    public String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl("");
        sc.append(type.getPrefix());
        sc.append(name);
        sc.appendnl(" {");

        attributes.forEach(h -> sc.appendnl(h.getHtmlRep()));

        sc.appendnl("}");

        return sc.getString();
    }

}
