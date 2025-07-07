/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Getter;

/**
 * A generic function definition to send to a receiver
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Getter
public abstract class AbsFunctionCall
{

    private final String   name;
    private final Object[] args;

    @JsonIgnore
    private Integer        paramsSet;

    /**
     * Construct a new instance
     */
    protected AbsFunctionCall()
    {
        this.name = functionName();
        this.args = new Object[numberOfParameters()];
        this.paramsSet = 0;
    }

    /**
     * Set the call name for this function
     * 
     * @return the call name of this function
     */
    protected abstract String functionName();

    /**
     * Set the number of expected parameters for consistency checking
     * 
     * @return the number of expected parameters
     */
    protected abstract Integer numberOfParameters();

    /**
     * Set the next {@link String} parameter for this function.
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setParam(String param)
    {
        setParameter(param);

        return this;
    }

    /**
     * Set the next {@link Integer} parameter for this function
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setParam(int param)
    {
        setParameter(param);

        return this;
    }

    /**
     * Set the next {@link Boolean} parameter for this function
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setParam(boolean param)
    {
        setParameter(param);

        return this;
    }

    /**
     * Set the next {@link Double} parameter for this function
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setParam(double param)
    {
        setParameter(param);

        return this;
    }

    protected final AbsFunctionCall setParam(AbsFunctionArgs args)
    {
        setParameter(args);

        return this;
    }

    /**
     * Internal function to add a parameter
     * 
     * @param param to add
     */
    private final void setParameter(Object param)
    {
        if (paramsSet >= args.length)
        {
            throw new IndexOutOfBoundsException("all params are already set");
        }

        args[paramsSet] = param;

        paramsSet++;
    }

    /**
     * Parse this function to JS style
     * 
     * @return a JS callable String
     */
    public final String parseAsJS()
    {
        StringConcat sc = new StringConcat();

        sc
            .append("CILIB.FunctionRegistry.call('")
            .append(name)
            .append("'")
            .append(args.length > 0, ", ")
            .append(
                Arrays
                    .stream(args)
                    .map(a -> {
                        if (a instanceof String s)
                        {
                            return StringAdapter.prefixAndSuffix("'", s, "'");
                        }
                        else
                        {
                            return String.valueOf(a);
                        }
                    })
                    .toList(),
                ", ")
            .append(");");

        return sc.toString();
    }

}
