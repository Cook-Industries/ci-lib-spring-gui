/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.List;

import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Builder;
import lombok.Singular;

/**
 *

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
