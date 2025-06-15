/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

import lombok.Data;

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
