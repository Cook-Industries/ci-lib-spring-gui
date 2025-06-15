/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.ArrayList;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;
import de.cookindustries.lib.spring.gui.util.StringConcat;

/**
 *

 */
public class HtmlSite
{

    private final ArrayList<HtmlHeadValue> headers;
    private final ArrayList<CSSLink>       css;
    private final ArrayList<CSSEntity>     cssInline;
    private final ArrayList<JsLink>        scripts;
    private final ArrayList<JsImport>      imports;
    private final ArrayList<Container>     content;

    HtmlSite(ArrayList<HtmlHeadValue> headers, ArrayList<CSSLink> css, ArrayList<CSSEntity> cssInline, ArrayList<JsLink> scripts,
        ArrayList<JsImport> imports, ArrayList<Container> content)
    {
        this.headers = headers;
        this.css = css;
        this.cssInline = cssInline;
        this.scripts = scripts;
        this.imports = imports;
        this.content = content;
    }

    public String getHtmlCode()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl("<!DOCTYPE html>");
        sc.appendnl("<html>");
        sc.appendnl("<head>");

        sc.appendnl("<base href=\"/\">");

        headers.forEach(h -> sc.append(h.getHtmlCode()));

        css.forEach(h -> sc.append(h.getHtmlCode()));

        sc.appendnl("<style>");

        cssInline.forEach(h -> sc.append(h.getHtmlRep()));

        sc.appendnl("</style>");

        sc.appendnl(JsImportMap.builder().entries(imports).build().getHtmlRep());

        scripts.forEach(h -> sc.append(h.getHtmlRep()));

        sc.appendnl("</head>");

        sc.appendnl("<body>");

        content.forEach(c -> sc.append(ContainerHtmlMapper.map(c)));

        sc.appendnl("</body>");
        sc.appendnl("</html>");

        return sc.getString();
    }
}
