/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import de.cookindustries.lib.spring.gui.html.HtmlExportable;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public final class ValuePair implements HtmlExportable
{

    private final String name;
    private final Object value;

    public final String getURLRep()
    {
        try
        {
            StringConcat sc = new StringConcat();

            sc.append(URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
            sc.append("=");

            if (value instanceof Integer integer)
            {
                sc.append(integer);
            }
            else
            {
                sc.append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.toString()));
            }

            return sc.getString();
        }
        catch (UnsupportedEncodingException ex)
        {
            // TODO: really? fix this exception...
            throw new RuntimeException("kaputt");
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getHtmlRep()
    {
        StringConcat sc = new StringConcat();

        sc.append(name);
        sc.append(": ");

        if (value instanceof Integer integer)
        {
            sc.append(integer);
        }
        else
        {
            sc.append("\"");
            sc.append(String.valueOf(value));
            sc.append("\"");
        }

        return sc.getString();
    }
}
