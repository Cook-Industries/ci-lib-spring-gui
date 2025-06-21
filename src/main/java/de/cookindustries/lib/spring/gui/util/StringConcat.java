/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@link String} concatenation with various convinience functions for on-the-fly complex String creation.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class StringConcat
{

    private final StringBuilder stb;

    /*
     * Create a new instance
     */
    public StringConcat()
    {
        stb = new StringBuilder();
    }

    /**
     * Append a {@link String} as-is.
     * 
     * @param string to append
     * @return this object for chaining
     */
    public StringConcat append(String string)
    {
        stb.append(string);

        return this;
    }

    /**
     * Append a {@link String} depending on a condition.
     * 
     * @param apply whether {@code string} should be appended
     * @param string to append if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat append(Boolean apply, String string)
    {
        if (apply.booleanValue())
        {
            stb.append(string);
        }

        return this;
    }

    /**
     * Append a {@link String} from a {@link Supplier} depending on a condition.
     * <p>
     * This is useful, if the creation of the String to append is costly.
     * 
     * @param apply whether {@code string} should be appended
     * @param supplier to call if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat append(Boolean apply, Supplier<String> supplier)
    {
        if (apply.booleanValue())
        {
            stb.append(supplier.get());
        }

        return this;
    }

    /**
     * Append multiple lines at once, each followed by a {@code newline}.
     * 
     * @param strings to append
     * @return this object for chaining
     */
    public StringConcat appendnl(String... strings)
    {
        for (String s : strings)
        {
            stb.append(s);
        }

        stb.append("\n");

        return this;
    }

    /**
     * Append a single {@link Character} as-is.
     * 
     * @param character to append
     * @return this object for chaining
     */
    public StringConcat append(Character character)
    {
        stb.append(character);

        return this;
    }

    /**
     * Append a {@link Integer} as-is.
     * 
     * @param integer to append
     * @return this object for chaining
     */
    public StringConcat append(Integer integer)
    {
        stb.append(integer);

        return this;
    }

    /**
     * Append a single line containing all elements from {@code items}, sepearated each by {@code separator}.
     * 
     * @param items to append
     * @param separator to insert between each {@code item}
     * @return this object for chaining
     */
    public StringConcat append(Collection<?> items, String separator)
    {
        append(
            items
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(separator)));

        return this;
    }

    /**
     * Append an {@link Enum} as a {@link String} via calling {@link Enum#name()}.
     * 
     * @param element to append
     * @return this object for chaining
     */
    @SuppressWarnings("rawtypes")
    public StringConcat append(Enum element)
    {
        stb.append(element.name());

        return this;
    }

    /**
     * Remove the last {@code count} of chars from the end.
     * 
     * @param count to remove from the end
     * @return this object for chaining
     */
    public StringConcat removeLastChars(Integer count)
    {
        stb.delete(stb.length() - count, stb.length());

        return this;
    }

    /**
     * Clear this object.
     * <p>
     * This can <b>not</b> be reversed!
     * 
     * @return this object for chaining
     */
    public StringConcat clear()
    {
        stb.delete(0, stb.length());

        return this;
    }

    /**
     * Check if this concat contains any value.
     * 
     * @return true, if nothing yet is added to this concatenation
     */
    public Boolean isEmpty()
    {
        return stb.isEmpty();
    }

    /**
     * Get the size - the number of chars - of this concatenation
     * 
     * @return the number of chars herein
     */
    public Integer size()
    {
        return stb.length();
    }

    /**
     * Get a single {@link String} representation of this concatenation.
     * 
     * @return a {@code String} representation of the value herein
     */
    public String getString()
    {
        return stb.toString();
    }
}
