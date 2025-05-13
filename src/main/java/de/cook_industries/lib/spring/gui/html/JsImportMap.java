/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

import java.util.List;

import de.cook_industries.lib.spring.gui.util.StringConcat;

import lombok.Builder;
import lombok.Singular;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@Builder
class JsImportMap implements HtmlExportable
{

    @Singular
    private final List<JsImport> entries;

    @Override
    public String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc.append("<script type=\"importmap\">");
        sc.append("{ \"imports\": {");

        Boolean addComma = false;

        for (JsImport imp : entries)
        {
            if (addComma)
            {
                sc.append(", ");
            }

            sc.append(imp.toString());
            addComma = true;
        }

        sc.append("}}");
        sc.append("</script>");

        return sc.getString();
    }
}
