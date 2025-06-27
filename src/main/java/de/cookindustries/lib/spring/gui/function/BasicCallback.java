package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BasicCallback extends AbsFunctionCall
{

    public BasicCallback(String requestUrl, Enum<?> action, List<ValuePair> parameters)
    {

        super();

        setStringParam(requestUrl);

        List<ValuePair> list = new ArrayList<>(parameters);
        list.add(0, new ValuePair("__action", action.name()));
        setStringParam(parameters.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")));
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
