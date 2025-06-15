/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.util.List;

import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class TranslationMapping
{

    private String                       language;
    private String                       country;
    private List<TranslationMappingText> elements;
}
