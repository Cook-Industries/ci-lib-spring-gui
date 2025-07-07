/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class SiteImports
{

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

}
