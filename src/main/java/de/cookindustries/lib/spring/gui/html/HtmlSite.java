/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.html.HtmlMapper;
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
    private final String                title;

    @Singular
    private final List<HtmlHeadValue>   headers;

    @Singular
    private final List<CSSLink>         cssLinks;

    @Singular
    private final List<CssEntity>       cssEntities;

    @Singular
    private final List<AbsJsLink>       jsScripts;

    @Singular
    private final List<JsImport>        jsImports;

    @Singular
    private final List<Container>       containers;

    @Singular
    private final List<AbsFunctionCall> functions;

    public String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc
            .appendnl("<!DOCTYPE html>")
            .appendnl("<html>")
            .appendnl("<head>")
            .appendnl("<base href=\"/\">")
            .appendnl(headers, HtmlHeadValue::getHtmlRep)
            .appendnl(cssLinks, CSSLink::getHtmlRep)
            .appendnl("<style>")
            .appendnl(cssEntities, CssEntity::toCssString)
            .appendnl("</style>")
            .appendnl(
                JsImportMap
                    .builder()
                    .entries(jsImports)
                    .build()
                    .getHtmlRep())
            .appendnl(jsScripts, AbsJsLink::getHtmlRep)
            .appendnl("</head>")
            .appendnl("<body>")
            .appendnl(containers, c -> HtmlMapper.map(c))
            .appendnl("<script>")
            .appendnl("document.addEventListener(\"DOMContentLoaded\", () => setTimeout(__onPageLoad, 1000));")
            .appendnl("function __onPageLoad() {")
            .append(functions, AbsFunctionCall::parseAsJS, title)
            .appendnl("}")
            .appendnl("</script>")
            .appendnl("</body>")
            .appendnl("</html>");

        return sc.toString();
    }
}
