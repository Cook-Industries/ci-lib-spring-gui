/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Static adapter class for {@link String} manipulation.
 * <p>
 * Each call to objects within the functions of this class will be made using {@link String#valueOf(Object)}.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class StringAdapter
{

    private StringAdapter()
    {}

    /**
     * Replace {@code \} with {@code /}.
     * 
     * @param path path to sanitize
     * @return the sanitized path
     */
    public static String sanitizePath(String path)
    {
        return path.replace('\\', '/');
    }

    /**
     * Prefix a {@link String} with a {@code prefix}.
     * 
     * @param prefix to put in front of the {@code content}
     * @param content to include
     * @return a {@code String} in form '{@code prefix} {@code content}'
     */
    public static String prefix(String prefix, String content)
    {
        return String.valueOf(prefix) + String.valueOf(content);
    }

    /**
     * Prefix a {@link String} with a {@code prefix}.
     * 
     * @param suffix to put behind of the {@code content}
     * @param content to include
     * @return a {@code String} in form '{@code content} {@code suffix}'
     */
    public static String suffix(String content, String suffix)
    {
        return String.valueOf(content) + String.valueOf(suffix);
    }

    /**
     * Prefix and suffix a {@link String} with a {@code prefix} and a {@code suffix}.
     * 
     * @param prefix to put in front of the {@code content}
     * @param content to include
     * @param suffix to put behind of the {@code content}
     * @return a {@code String} in form '{@code prefix} {@code content} {@code suffix}'
     */
    public static String prefixAndSuffix(String prefix, String content, String suffix)
    {
        return String.valueOf(prefix) + String.valueOf(content) + String.valueOf(suffix);
    }

    /**
     * Traverse a {@link Collection} of {@code item}s and prefix each with a {@code prefix}.
     * 
     * @param items to traverse
     * @param prefix to put in front of each {@code item}
     * @return a {@code Collection<String>} with each item in form '{@code prefix} {@code item}'
     */
    public static Collection<String> prefix(Collection<?> items, String prefix)
    {
        return items == null
            ? List.of()
            : items
                .stream()
                .map(i -> prefix(prefix, String.valueOf(i)))
                .collect(Collectors.toList());
    }

    /**
     * Traverse a {@link Collection} of {@code item}s and suffix each with a {@code suffix}.
     * 
     * @param items to traverse
     * @param suffix to put after each {@code item}
     * @return a {@code List<String>} with each item in form '{@code item} {@code suffix}'
     */
    public static Collection<String> suffix(Collection<?> items, String suffix)
    {
        return items == null
            ? List.of()
            : items
                .stream()
                .map(i -> suffix(String.valueOf(i), suffix))
                .collect(Collectors.toList());
    }

    /**
     * Traverse a {@link Collection} of {@code item}s and prefix each with a {@code prefix} and suffix each with a {@code suffix}.
     * 
     * @param items to traverse
     * @param prefix to put in front of each {@code item}
     * @param suffix to put after each {@code item}
     * @return a {@code List<String>} with each item in form '{@code prefix} {@code item} {@code suffix}'
     */
    public static Collection<String> prefixAndSuffix(Collection<?> items, String prefix, String suffix)
    {
        return items == null
            ? List.of()
            : items
                .stream()
                .map(i -> prefixAndSuffix(prefix, String.valueOf(i), suffix))
                .collect(Collectors.toList());
    }

    /**
     * Traverse a {@link Collection} of {@code item}s and join them with a {@code separator}.
     * 
     * @param items to traverse
     * @param separator to join each {@code item} to the next
     * @return a {@code String} in form '{@code item} {@code separator} {@code item} [...] {@code item}'
     */
    public static String separate(Collection<?> items, String separator)
    {
        return items == null
            ? ""
            : items
                .stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(separator));
    }

    /**
     * Traverse a {@link Object} array and join each {@code item} with a {@code separator}.
     * 
     * @param items to traverse
     * @param separator to join each {@code item} to the next
     * @return a {@code String} in form '{@code item} {@code separator} {@code item} [...] {@code item}'
     */
    public static String separate(String separator, Object... items)
    {
        return items == null
            ? ""
            : Arrays
                .stream(items)
                .map(String::valueOf)
                .collect(Collectors.joining(separator));
    }

    /**
     * Traverse a {@link Collection} of {@code item}s and join them together.
     * 
     * @param items to traverse
     * @return a {@code String} in form '{@code item} {@code item} [...] {@code item}'
     */
    public static String from(Collection<?> items)
    {
        return separate(items, "");
    }

    /**
     * Traverse a {@link Object} array of {@code item}s and join them together.
     * 
     * @param items to traverse
     * @return a {@code String} in form '{@code item} {@code item} [...] {@code item}'
     */
    public static String from(Object... items)
    {
        return separate("", items);
    }
}
