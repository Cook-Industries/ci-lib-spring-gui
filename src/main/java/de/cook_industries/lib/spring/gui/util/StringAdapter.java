/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public final class StringAdapter
{

    private StringAdapter()
    {}

    public static String withPrefix(String prefix, String content)
    {
        return prefix + content;
    }

    public static String withSuffix(String content, String suffix)
    {
        return content + suffix;
    }

    public static String withPrefixAndSuffix(String prefix, String content, String suffix)
    {
        return prefix + content + suffix;
    }

    public static Collection<String> withPrefix(Collection<?> items, String prefix)
    {
        return items.stream().map(i -> withPrefix(i.toString(), prefix)).collect(Collectors.toList());
    }

    public static Collection<String> withSuffix(Collection<?> items, String suffix)
    {
        return items.stream().map(i -> withSuffix(i.toString(), suffix)).collect(Collectors.toList());
    }

    public static Collection<String> withPrefixAndSuffix(Collection<?> items, String prefix, String suffix)
    {
        return items.stream().map(i -> withPrefixAndSuffix(i.toString(), prefix, suffix)).collect(Collectors.toList());
    }

    public static String withSeparatorFrom(Collection<?> items, String separator)
    {
        return items.stream().map(Object::toString).collect(Collectors.joining(separator));
    }

    public static String withSeparatorFrom(String separator, Object... items)
    {
        return Arrays.stream(items).map(Object::toString).collect(Collectors.joining(separator));
    }

    public static String from(Collection<?> items)
    {
        return withSeparatorFrom(items, "");
    }

    public static String from(Object... items)
    {
        return withSeparatorFrom("", items);
    }
}
