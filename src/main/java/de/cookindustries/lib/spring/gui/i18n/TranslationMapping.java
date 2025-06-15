/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.util.List;

import lombok.Data;

@Data
public class TranslationMapping
{

    private String                       language;
    private String                       country;
    private List<TranslationMappingText> elements;
}
