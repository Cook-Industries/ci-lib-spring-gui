/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;

import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Data;

/**
 * A generic function definition to send to the receiver
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public abstract class FunctionCall
{

    private final String   functionName;
    private final Object[] parameters;
    private Integer        paramsSet;

    /**
     * Construct a new instance
     */
    protected FunctionCall()
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

    protected final FunctionCall setStringParam(String param)
    {
        setParameter(paramsSet++, param);

        return this;
    }

    protected final FunctionCall setIntegerParam(Integer param)
    {
        setParameter(paramsSet++, param);

        return this;
    }

    protected final FunctionCall setBooleanParam(Boolean param)
    {
        setParameter(paramsSet++, param);

        return this;
    }

    protected final FunctionCall setDoubleParam(Double param)
    {
        setParameter(paramsSet++, param);

        return this;
    }

    private final void setParameter(Integer pos, Object param)
    {
        if (paramsSet >= parameters.length)
        {
            throw new IndexOutOfBoundsException("all params are already set");
        }

        parameters[pos] = param;
    }

    /**
     * Parse this function to JS style
     * 
     * @return a JS callable String
     */
    public final String parse()
    {
        StringConcat sc     = new StringConcat();

        List<String> params = new ArrayList<>();

        for (Object o : parameters)
        {
            String className = o.getClass().getSimpleName();

            switch (className)
            {
                case "Integer":
                case "Boolean":
                    params.add(o.toString());
                    break;

                case "String":
                    params.add(StringAdapter.withPrefixAndSuffix("'", o.toString(), "'"));
                    break;

                default:
                    throw new IllegalArgumentException("not recognised class type as call parameter " + className);
            }
        }

        sc.append(functionName);
        sc.append("(");
        sc.append(params, ", ");
        sc.append(")");
        sc.append(";");

        return sc.getString();
    }

}
