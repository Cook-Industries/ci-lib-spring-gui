/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;

/**
 * A generic function definition to send to a receiver
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public abstract class AbsFunctionCall
{

    private final String   functionName;
    private final Object[] parameters;
    private Integer        paramsSet;

    /**
     * Construct a new instance
     */
    protected AbsFunctionCall()
    {
        this.functionName = functionName();
        this.parameters = new Object[numberOfParameters()];
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
     * Set the next {@link String} parameter for this function where {@code param} is pre- and suffixed with '.
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setStringParam(String param)
    {
        return setStringParam(param, true);
    }

    /**
     * Set the next {@link String} parameter for this function.
     * 
     * @param param to set
     * @param escape whether {@code param} should be pre- and suffixed with '
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setStringParam(String param, boolean escape)
    {
        if (escape)
        {
            setParameter(StringAdapter.prefixAndSuffix("'", param, "'"));
        }
        else
        {
            setParameter(param);
        }

        return this;
    }

    /**
     * Set the next {@link Integer} parameter for this function
     * 
     * @param param to set
     * @return {@code this} for chaining
     */
    protected final AbsFunctionCall setIntegerParam(Integer param)
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
    protected final AbsFunctionCall setBooleanParam(Boolean param)
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
    protected final AbsFunctionCall setDoubleParam(Double param)
    {
        setParameter(param);

        return this;
    }

    /**
     * Internal function to add a parameter
     * 
     * @param param to add
     */
    private final void setParameter(Object param)
    {
        if (paramsSet >= parameters.length)
        {
            throw new IndexOutOfBoundsException("all params are already set");
        }

        parameters[paramsSet] = param;

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

        sc.append(functionName);
        sc.append("(");
        sc.append(parameters, ", ");
        sc.append(");");

        return sc.toString();
    }

}
