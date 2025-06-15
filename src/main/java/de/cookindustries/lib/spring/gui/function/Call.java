/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;

import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Data;

@Data
public abstract class Call
{

    private final String   functionName;
    private final Object[] parameters;

    protected Call(String functionName, Integer paramCnt)
    {
        this.functionName = functionName;
        this.parameters = new Object[paramCnt];
    }

    protected void setStringParam(Integer pos, Integer param)
    {
        setParameter(pos, param);
    }

    protected void setIntegerParam(Integer pos, Integer param)
    {
        setParameter(pos, param);
    }

    protected void setBooleanParam(Integer pos, Boolean param)
    {
        setParameter(pos, param);
    }

    protected void setDoubleParam(Integer pos, Double param)
    {
        setParameter(pos, param);
    }

    private void setParameter(Integer pos, Object param)
    {
        if (pos < 0 || pos >= parameters.length)
        {
            throw new IndexOutOfBoundsException("not enough prameters expected");
        }

        parameters[pos] = param;
    }

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
                    throw new IllegalArgumentException("no recognised class type as call parameter" + className);
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
