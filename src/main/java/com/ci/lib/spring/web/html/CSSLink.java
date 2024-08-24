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
class CSSLink
{

    private final String href;

    public CSSLink(String href)
    {
        this.href = href;
    }

    String getHtmlCode()
    {
        return String.format("<link href=\"%s\" rel=\"stylesheet\">", href);
    }
}
