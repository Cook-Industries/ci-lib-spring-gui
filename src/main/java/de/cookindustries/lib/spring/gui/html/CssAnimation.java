/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import java.util.Map;
import java.util.Map.Entry;

import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;

/**
 * Defines a CSS animation {@code @keyframes} object.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@Jacksonized
public class CssAnimation
{

    private static final String                    INDENT = "    ";

    @NonNull
    private final String                           name;

    @Singular
    private final Map<String, Map<String, String>> keyframes;

    /**
     * Create a css object representation of this instance
     * 
     * @return this instance as a css string
     */
    public String toCssString()
    {
        StringConcat sc = new StringConcat();

        sc.appendnl();
        sc.append("@keyframes ");
        sc.appendnl(name);
        sc.appendnl("{");

        keyframes
            .entrySet()
            .stream()
            .forEach(kf -> {
                sc.append(INDENT);
                sc.append(kf.getKey());
                sc.appendnl(" {");
                kf
                    .getValue()
                    .entrySet()
                    .stream()
                    .sorted(Entry.comparingByKey())
                    .forEach(a -> sc.appendnl(String.format("%s%s: %s;", INDENT.repeat(2), a.getKey(), a.getValue())));
                sc.append(INDENT);
                sc.appendnl("}");
            });

        sc.appendnl("}");

        return sc.toString();
    }

}
