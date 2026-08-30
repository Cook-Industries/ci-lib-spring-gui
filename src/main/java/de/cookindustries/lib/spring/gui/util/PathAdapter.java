/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

public final class PathAdapter
{

    private PathAdapter()
    {}

    /**
     * Replace {@code \} with {@code /}.
     * 
     * @param path to sanitize
     * @return the sanitized {@code path}
     */
    public static String toUnixTypePath(String path)
    {
        return path.replace('\\', '/');
    }

    /**
     * Remove a preface {@code /} if present.
     * 
     * @param path to sanitize
     * @return {@code path} without {@code /} as first character
     */
    public static String removePrefaceSlash(String path)
    {
        if (path.startsWith("/"))
        {
            return path.substring(1);
        }

        return path;
    }

    public static String toUnixAndRemovePrefaceSlash(String path)
    {
        return removePrefaceSlash(toUnixTypePath(path));
    }

}
