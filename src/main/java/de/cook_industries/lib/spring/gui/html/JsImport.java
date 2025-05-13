/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

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
