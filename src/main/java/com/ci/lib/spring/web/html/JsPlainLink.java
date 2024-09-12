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
public class JsPlainLink implements JsLink
{

    private final String href;

    public JsPlainLink(String href)
    {
        this.href = href;
    }

    @Override
    public String getHtmlRep()
    {
        return String.format("<script src=\"%s\"></script>", href);
    }
}
