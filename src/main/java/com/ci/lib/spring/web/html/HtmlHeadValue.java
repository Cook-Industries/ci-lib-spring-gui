/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.html;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
abstract class HtmlHeadValue
{

    protected final Header type;
    protected final String value;

    protected HtmlHeadValue(Header type, String value)
    {
        this.type = type;
        this.value = value;
    }

    abstract String getHtmlCode();
}
