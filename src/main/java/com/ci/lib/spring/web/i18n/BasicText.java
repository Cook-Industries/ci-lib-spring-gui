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
public enum BasicText {

    TEXT_NOT_SET(0, "TNS"), OK(1, "ok"), CANCEL(2, "cancel"), HELP(3, "help"), CLOSE(4, "close");

    private final Integer id;
    private final String  fallback;

    BasicText(Integer id, String fallback)
    {
        this.id = id;
        this.fallback = fallback;
    }

    public final Integer getID()
    {
        return id;
    }

    public final String getFallback()
    {
        return fallback;
    }
}
