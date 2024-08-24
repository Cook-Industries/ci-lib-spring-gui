/**
 * Copyright(c) 2021 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 22.11.2021 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.html;

import java.util.ArrayList;

import com.ci.lib.spring.web.hmi.container.Container;
import com.ci.lib.spring.web.hmi.mapper.HtmlMapper;
import com.ci.lib.spring.web.util.StringConcat;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
public class HtmlSite
{

    private ArrayList<HtmlHeadValue> headers;
    private ArrayList<CSSLink>       css;
    private ArrayList<CSSEntity>     cssInline;
    private ArrayList<JsLink>        scripts;

    private ArrayList<Container>     content;

    HtmlSite(ArrayList<HtmlHeadValue> headers, ArrayList<CSSLink> css, ArrayList<CSSEntity> cssInline, ArrayList<JsLink> scripts,
            ArrayList<Container> content)
    {
        this.headers = headers;
        this.css = css;
        this.cssInline = cssInline;
        this.scripts = scripts;
        this.content = content;
    }

    public String getHtmlCode()
    {
        StringConcat s = new StringConcat();

        s.appendnl("<!DOCTYPE html>");
        s.appendnl("<html>");
        s.appendnl("<head>");

        headers.forEach(h -> s.append(h.getHtmlCode()));

        css.forEach(h -> s.append(h.getHtmlCode()));

        s.appendnl("<style>");

        cssInline.forEach(h -> s.append(h.getHtmlRep()));

        s.appendnl("</style>");

        scripts.forEach(h -> s.append(h.getHtmlCode()));

        s.appendnl("</head>");

        s.appendnl("<body>");

        content.forEach(c -> s.append(HtmlMapper.map(c)));

        s.appendnl("</body>");
        s.appendnl("</html>");

        return s.getString();
    }
}
