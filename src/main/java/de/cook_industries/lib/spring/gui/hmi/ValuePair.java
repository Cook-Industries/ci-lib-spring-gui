/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import de.cook_industries.lib.spring.gui.html.HtmlExportable;
import de.cook_industries.lib.spring.gui.util.StringConcat;

public final class ValuePair implements HtmlExportable
{

    private final String name;
    private final Object value;

    public ValuePair(String name, Object value)
    {
        this.name = name;
        this.value = value;
    }

    public final String getName()
    {
        return name;
    }

    public final Object getValue()
    {
        return value;
    }

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
                sc.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.toString()));
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
     * {@inheritdoc}
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
            sc.append(value.toString());
            sc.append("\"");
        }

        return sc.getString();
    }
}
