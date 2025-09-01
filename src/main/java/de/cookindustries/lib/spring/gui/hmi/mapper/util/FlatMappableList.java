/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * A implementation of a {@code List} holder class to ensure Immutability and safe class parsing.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@ToString
@Jacksonized
public class FlatMappableList<T extends FlatMappable> extends AbstractList<T>
{

    @Singular
    private final List<T> elements;

    public FlatMappableList(List<T> items)
    {
        this.elements = Collections.unmodifiableList(new ArrayList<>(items));
    }

    @Override
    public T get(int index)
    {
        return elements.get(index);
    }

    @Override
    public int size()
    {
        return elements.size();
    }

    @Override
    public boolean add(T t)
    {
        throw new UnsupportedOperationException("FixedList is immutable");
    }

    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException("FixedList is immutable");
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("FixedList is immutable");
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException("FixedList is immutable");
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("FixedList is immutable");
    }
}
