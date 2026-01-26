/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@EqualsAndHashCode
@Jacksonized
public final class CssEntity
{

    private static final String       INDENT = "    ";

    @NonNull
    private final String              selector;

    @Singular
    private final Map<String, String> attributes;

    private final boolean             append;

    public CssEntity merge(CssEntity other)
    {
        if (!selector.equals(other.getSelector()))
        {
            throw new IllegalArgumentException("selector not identical");
        }

        Map<String, String> map = new HashMap<>();

        other
            .getAttributes()
            .forEach((key, val) -> map.put(key, val));

        getAttributes()
            .forEach((key, val) -> map.put(key, val));

        return CssEntity
            .builder()
            .selector(getSelector())
            .attributes(map)
            .build();
    }

    /**
     * Create a css object representation of this instance
     * 
     * @return this instance as a css string
     */
    public String toCssString()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl();
        sc.appendnl(selector);
        sc.appendnl("{");

        attributes
            .entrySet()
            .stream()
            .sorted(Entry.comparingByKey())
            .forEach(a -> sc.appendnl(String.format("%s%s: %s;", INDENT, a.getKey(), a.getValue())));

        sc.appendnl("}");

        return sc.toString();
    }

    /**
     * Get a {@link Comparator} for these entities which first orders them by:
     * <ul>
     * <li>:</li>
     * <li>.</li>
     * <li>#</li>
     * </ul>
     * and then by {@code name}.
     * 
     * @return the setup {@code Comparator}
     */
    public static Comparator<CssEntity> compareByFirstChar()
    {
        return Comparator
            .comparingInt(CssEntity::getNamePriority)
            .thenComparing(CssEntity::getSelector);
    }

    /**
     * Determines the priority group based on the first character of the name.
     */
    private int getNamePriority()
    {
        char firstChar = selector.charAt(0);

        if (firstChar == ':')
        {
            return 0;
        }

        if (Character.isLetter(firstChar))
        {
            return 1;
        }

        if (firstChar == '.')
        {
            return 2;
        }

        if (firstChar == '#')
        {
            return 3;
        }

        return 4;
    }

}
