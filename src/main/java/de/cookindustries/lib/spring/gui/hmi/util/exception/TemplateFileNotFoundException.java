/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util.exception;

import lombok.Getter;

@Getter
public class TemplateFileNotFoundException extends RuntimeException
{

    private final String templatePath;

    public TemplateFileNotFoundException(String path)
    {
        super(String.format("no template file under [%s] found", path));

        templatePath = path;
    }
}
