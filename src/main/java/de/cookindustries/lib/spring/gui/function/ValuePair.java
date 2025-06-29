/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import de.cookindustries.lib.spring.gui.util.StringConcat;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class ValuePair
{

    private final String name;
    private final Object value;

    public ValuePair(String name, String value)
    {
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(value, "value can not be null");

        this.name = name;
        this.value = value;
    }

    public ValuePair(String name, Integer value)
    {
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(value, "value can not be null");

        this.name = name;
        this.value = value;
    }

    public ValuePair(String name, Double value)
    {
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(value, "value can not be null");

        this.name = name;
        this.value = value;
    }

    public ValuePair(String name, Boolean value)
    {
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(value, "value can not be null");

        this.name = name;
        this.value = value;
    }

    private String urlEncode(String value)
    {
        try
        {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("this exception can not be thrown!");
        }
    }

    /**
     * Export this ValuePair as &gt;&quot;{@code name}&quot;: &quot;{@code value}&quot;&lt; or &gt;&quot;{@code name}&quot;:
     * {@code value}&lt; depending on type of {@code value}.
     * 
     * @return representation of this in json parameter form
     */
    public final String exportInJsonNotation()
    {
        return String.format("\"%s\": \"%s\"", name.replace("\"", ""), String.valueOf(value).replace("\"", "\\\""));
    }

    /**
     * Export this {@code ValuePair} as '{@code name}={@code value}' with both parts url encoded against UTF-8.
     * 
     * @return representation of this in form '{@code name}={@code value}'
     */
    public final String exportInUrlEndoding()
    {
        StringConcat sc = new StringConcat();

        sc.append(urlEncode(name));
        sc.append("=");

        if (value instanceof Integer integer)
        {
            sc.append(integer);
        }
        else
        {
            sc.append(urlEncode(String.valueOf(value)));
        }

        return sc.toString();

    }
}
