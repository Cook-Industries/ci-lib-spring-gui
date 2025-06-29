package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A generic function definition to send back to the server
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class BasicServerCall extends AbsFunctionCall
{

    /**
     * @param requestUrl
     * @param action
     * @param parameters
     */
    public BasicServerCall(String requestUrl, Enum<?> action, List<ValuePair> jsonObjectParameters)
    {
        super();

        setStringParam(requestUrl);

        List<ValuePair> list = new ArrayList<>(jsonObjectParameters);
        list.add(0, new ValuePair("__action", action.name()));
        setStringParam(list.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")), false);
    }

    @Override
    protected String functionName()
    {
        return "call";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }
}
