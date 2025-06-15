/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

public enum ButtonClass {

    DEFAULT("btn-primary"), SUCCESS("btn-success");

    private final String className;

    private ButtonClass(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
