/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

import java.util.ArrayList;

import de.cook_industries.lib.spring.gui.hmi.container.Container;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
public class HtmlFileBuilder
{

    private final ArrayList<HtmlHeadValue> headers;
    private final ArrayList<CSSLink>       css;
    private final ArrayList<CSSEntity>     cssInline;
    private final ArrayList<JsImport>      importEntries;
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
        importEntries = new ArrayList<>();
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

    public HtmlFileBuilder importMap(JsImport jsImport)
    {
        importEntries.add(jsImport);
        return this;
    }

    public HtmlFileBuilder script(JsLink jsLink)
    {
        scripts.add(jsLink);

        return this;
    }

    public HtmlFileBuilder content(Container container)
    {
        content.add(container);

        return this;
    }

    public HtmlSite build()
    {
        return new HtmlSite(headers, css, cssInline, scripts, importEntries, content);
    }
}
