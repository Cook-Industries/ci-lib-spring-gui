/**
 * Copyright(c) 2021 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 22.11.2021 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
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
