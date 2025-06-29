/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@NoArgsConstructor
public class TranslationMappingText
{

    /** key for the translation */
    private String key;

    /** the translated text */
    private String text;

}
