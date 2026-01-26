/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class JsImport
{

    private final String name;
    private final String src;

    @Override
    public String toString()
    {
        return String.format("\"%s\": \"%s\"", name, src);
    }
}
