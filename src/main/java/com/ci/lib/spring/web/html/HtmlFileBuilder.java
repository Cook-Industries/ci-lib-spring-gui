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

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
public class HtmlFileBuilder
{

    private final ArrayList<HtmlHeadValue> headers;
    private final ArrayList<CSSLink>       css;
    private final ArrayList<CSSEntity>     cssInline;
    private final ArrayList<JsLink>        scripts;

    private final ArrayList<Container>     content;

    public static HtmlFileBuilder site()
    {
        return new HtmlFileBuilder();
    }

    private HtmlFileBuilder()
    {
        headers = new ArrayList<>();
        css = new ArrayList<>();
        cssInline = new ArrayList<>();
        scripts = new ArrayList<>();
        content = new ArrayList<>();
    }

    public HtmlFileBuilder title(String title)
    {
        headers.add(new HeadTitle(title));

        return this;
    }

    public HtmlFileBuilder css(String href)
    {
        this.css.add(new CSSLink(href));

        return this;
    }

    public HtmlFileBuilder css(CSSEntity entity)
    {
        this.cssInline.add(entity);
        return this;
    }

    public HtmlFileBuilder script(String href)
    {
        scripts.add(new JsLink(href));

        return this;
    }

    public HtmlFileBuilder content(Container container)
    {
        content.add(container);

        return this;
    }

    public HtmlSite build()
    {
        return new HtmlSite(headers, css, cssInline, scripts, content);
    }
}
