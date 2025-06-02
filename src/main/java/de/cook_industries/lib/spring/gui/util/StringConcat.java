/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
public class StringConcat
{

    private final StringBuilder stb;

    public StringConcat()
    {
        stb = new StringBuilder();
    }

    public StringConcat append(String str)
    {
        stb.append(str);

        return this;
    }

    public StringConcat append(Boolean apply, String str)
    {
        if (apply.booleanValue())
        {
            stb.append(str);
        }

        return this;
    }

    public StringConcat append(Boolean apply, Supplier<String> supplier)
    {
        if (apply.booleanValue())
        {
            stb.append(supplier.get());
        }

        return this;
    }

    public StringConcat appendnl(String... str)
    {
        for (String s : str)
        {
            stb.append(s);
        }

        stb.append("\n");

        return this;
    }

    public StringConcat append(Character c)
    {
        stb.append(c);

        return this;
    }

    public StringConcat append(Integer i)
    {
        stb.append(i);

        return this;
    }

    public StringConcat append(Collection<?> items, String separator)
    {
        append(items.stream().map(Object::toString).collect(Collectors.joining(separator)));

        return this;
    }

    /**
     * @param e
     * @return
     */
    @SuppressWarnings("rawtypes")
    public StringConcat append(Enum e)
    {
        stb.append(e);

        return this;
    }

    public StringConcat removeLastChars(Integer i)
    {
        stb.delete(stb.length() - i, stb.length());

        return this;
    }

    public StringConcat clear()
    {
        stb.delete(0, stb.length());

        return this;
    }

    public Boolean isEmpty()
    {
        return stb.length() == 0;
    }

    public Integer size()
    {
        return stb.length();
    }

    public String getString()
    {
        return stb.toString();
    }
}
