/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.i18n;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
public enum Locale {

    DE_DE("de_DE"), EN_EN("en_EN");

    private final String lcid;

    /**
     * Constructs a Local identifier
     *
     * @param lcid - locale code in form "xx_YY" where xx is the language identifier and YY the country
     *        identifier
     */
    Locale(String lcid)
    {
        this.lcid = lcid;
    }

    /**
     *
     * @return
     */
    public final String getLcid()
    {
        return lcid;
    }
}
