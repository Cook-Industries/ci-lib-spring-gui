/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class CSSEntiyBuilder
{

    private final String             name;
    private final CSSEntityType      type;
    private final List<CSSAttribute> attributes;

    CSSEntiyBuilder(String name, CSSEntityType type)
    {
        this.name = name;
        this.type = type;
        this.attributes = new ArrayList<>();
    }

    public CSSEntiyBuilder attribute(CSSAttribute attribute)
    {
        attributes.add(attribute);

        return this;
    }

    public final CSSEntity build()
    {
        return new CSSEntity(name, type, Collections.unmodifiableList(attributes));
    }
}
