/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.i18n;

import lombok.Data;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@Data
public class I18nText
{

    private String key;
    private String language;
    private String country;
    private String text;

}
