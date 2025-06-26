/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter(value = AccessLevel.PACKAGE)
public class HtmlSite implements HtmlExportable
{

    @NonNull
    private final String              title;

    @Singular
    private final List<HtmlHeadValue> headers;

    @Singular
    private final List<CSSLink>       cssLinks;

    @Singular
    private final List<CSSEntity>     cssEntities;

    @Singular
    private final List<AbsJsLink>     jsScripts;

    @Singular
    private final List<JsImport>      jsImports;

    @Singular
    private final List<Container>     containers;

    public String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl("<!DOCTYPE html>");
        sc.appendnl("<html>");
        sc.appendnl("<head>");

        sc.appendnl("<base href=\"/\">");

        headers.forEach(h -> sc.append(h.getHtmlRep()));

        cssLinks.forEach(h -> sc.append(h.getHtmlRep()));

        sc.appendnl("<style>");

        cssEntities.forEach(h -> sc.append(h.getHtmlRep()));

        sc.appendnl("</style>");

        sc.appendnl(
            JsImportMap
                .builder()
                .entries(jsImports)
                .build()
                .getHtmlRep());

        jsScripts.forEach(h -> sc.append(h.getHtmlRep()));

        sc.appendnl("</head>");

        sc.appendnl("<body>");

        containers.forEach(c -> sc.append(ContainerHtmlMapper.map(c)));

        sc.appendnl("</body>");
        sc.appendnl("</html>");

        return sc.getString();
    }
}
