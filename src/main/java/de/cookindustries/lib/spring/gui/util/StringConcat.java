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
import java.util.Objects;
import java.util.function.Function;
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
     * Append a {@code new-line}.
     */
    public void nl()
    {
        stb.append(System.lineSeparator());
    }

    /**
     * Append a {@link String} as-is.
     * 
     * @param string to append
     * @return this object for chaining
     */
    public StringConcat append(String string)
    {
        stb.append(String.valueOf(string));

        return this;
    }

    /**
     * Append a {@link String} as-is followed by a {@code new-line}.
     * 
     * @param string to append
     * @return this object for chaining
     */
    public StringConcat appendnl(String string)
    {
        append(string);
        nl();

        return this;
    }

    /**
     * Append a {@link String} depending on a condition.
     * 
     * @param apply whether {@code string} should be appended
     * @param string to append if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat append(boolean apply, String string)
    {
        if (apply)
        {
            append(string);
        }

        return this;
    }

    /**
     * Append a {@link String} followed by a {@code new-line} depending on a condition.
     * 
     * @param apply whether {@code string} should be appended
     * @param string to append if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat appendnl(boolean apply, String string)
    {
        if (apply)
        {
            append(string);
            nl();
        }

        return this;
    }

    /**
     * Append a {@link String} from a {@link Supplier} depending on a condition.
     * <p>
     * Use this if the creation of the {@code String} to append is costly.
     * 
     * @param apply whether {@code string} should be appended
     * @param supplier {@code non-null} {@code Supplier} to call if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat append(boolean apply, Supplier<String> supplier)
    {
        if (apply && Objects.nonNull(supplier))
        {
            append(supplier.get());
        }

        return this;
    }

    /**
     * Append a {@link String} from a {@link Supplier} followed by a {@code new-line} depending on a condition.
     * <p>
     * Use this if the creation of the {@code String} to append is costly.
     * 
     * @param apply whether {@code string} should be appended
     * @param supplier {@code non-null} {@code Supplier} to call if {@code apply} is {@code true}
     * @return this object for chaining
     */
    public StringConcat appendnl(boolean apply, Supplier<String> supplier)
    {
        if (apply && Objects.nonNull(supplier))
        {
            append(supplier.get());
            nl();
        }

        return this;
    }

    /**
     * Append an {@link Enum} as a {@link String} via {@link Enum#name()}.
     * 
     * @param element to append
     * @return this object for chaining
     */
    @SuppressWarnings("rawtypes")
    public StringConcat append(Enum element)
    {
        append(element.name());

        return this;
    }

    /**
     * Append an {@link Enum} as a {@link String} via {@link Enum#name()} followed by a {@code new-line}.
     * 
     * @param element to append
     * @return this object for chaining
     */
    @SuppressWarnings("rawtypes")
    public StringConcat appendnl(Enum element)
    {
        append(element.name());

        nl();

        return this;
    }

    /**
     * Append a single {@link Character} as-is.
     * 
     * @param character {@code non-null} {@code Character} to append
     * @return this object for chaining
     */
    public StringConcat append(Character character)
    {
        stb.append(String.valueOf(character));

        return this;
    }

    /**
     * Append a single {@link Character} as-is followed by a {@code new-line}.
     * 
     * @param character {@code non-null} {@code Character} to append
     * @return this object for chaining
     */
    public StringConcat appendnl(Character character)
    {
        append(character);
        nl();

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
        stb.append(String.valueOf(integer));

        return this;
    }

    /**
     * Append a {@link Integer} as-is followed by a {@code new-line}.
     * 
     * @param integer to append
     * @return this object for chaining
     */
    public StringConcat appendnl(Integer integer)
    {
        append(integer);
        nl();

        return this;
    }

    /**
     * Append a single line containing all elements from {@code items}, sepearated each by {@code separator}.
     * <p>
     * If either of the parameters is {@code null} this method does nothing and just returns.
     * 
     * @param items {@code non-null} collection to append (items themselves can be {@code null})
     * @param separator {@code non-null} {@code separator} to insert between each {@code item}
     * @return this object for chaining
     */
    public <T> StringConcat append(Collection<T> items, String separator)
    {
        if (Objects.nonNull(items) && Objects.nonNull(separator))
        {
            append(
                items
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(separator)));
        }

        return this;
    }

    /**
     * Append all elements from {@code items} with the content derived by {@code function}, joined by {@code separator}.
     * <p>
     * If either of the parameters is {@code null} this method does nothing and just returns.
     * 
     * @param items {@code non-null} collection to append (items themselves can be {@code null})
     * @param function to retrive a {@code String} from each {@code item}
     * @param separator {@code non-null} {@code separator} to insert between each {@code item}
     * @return this object for chaining
     */
    public <T> StringConcat append(Collection<T> items, Function<T, String> function, String separator)
    {
        if (Objects.nonNull(items) && Objects.nonNull(separator))
        {
            append(
                items
                    .stream()
                    .map(function)
                    .collect(Collectors.joining(separator)));
        }

        return this;
    }

    /**
     * Append all elements from {@code items} with the content derived by {@code function}, each on a new line.
     * <p>
     * If either of the parameters is {@code null} this method does nothing and just returns.
     * 
     * @param items {@code non-null} collection to append (items themselves can be {@code null})
     * @param function to retrive a {@code String} from each {@code item}
     * @return this object for chaining
     */
    public <T> StringConcat appendnl(Collection<T> items, Function<T, String> function)
    {
        return append(items, function, "\n");
    }

    /**
     * Append a single line containing all elements from {@code objects}, sepearated each by {@code separator}.
     * <p>
     * If either of the parameters is {@code null} this method does nothing and just returns.
     * 
     * @param objects {@code non-null} array to append. (values can be {@code null})
     * @param separator {@code non-null} {@code separator} to insert between each {@code object}
     * @return this object for chaining
     */
    public StringConcat append(Object[] objects, String separator)
    {
        if (Objects.nonNull(objects) && Objects.nonNull(separator))
        {
            append(
                Arrays.stream(objects)
                    .map(String::valueOf)
                    .collect(Collectors.joining(separator)));
        }

        return this;
    }

    /**
     * Append multiple lines at once, each followed by a {@code new-line}.
     * 
     * @param strings to append
     * @return this object for chaining
     */
    public StringConcat appendnl(String... strings)
    {
        for (String s : strings)
        {
            append(s);
            nl();
        }

        return this;
    }

    /**
     * Remove the last {@code count} of chars from the end of this concatenation.
     * 
     * @param count to remove from the end
     * @return this object for chaining
     */
    public StringConcat removeLastChars(int count)
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
    public boolean isEmpty()
    {
        return stb.isEmpty();
    }

    /**
     * Get the size - the number of chars - of this concatenation
     * 
     * @return the number of chars herein
     */
    public int size()
    {
        return stb.length();
    }

    @Override
    public String toString()
    {
        return stb.toString();
    }
}
