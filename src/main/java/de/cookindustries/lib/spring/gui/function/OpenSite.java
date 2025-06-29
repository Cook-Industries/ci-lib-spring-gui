package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpenSite<T> extends AbsFunctionCall
{

    public OpenSite(String requestUrl, List<ValuePair> parameters)
    {
        this(requestUrl, null, parameters);
    }

    public OpenSite(String requestUrl, T uid, List<ValuePair> jsonObjectParameters)
    {

        super();

        setStringParam(requestUrl);

        List<ValuePair> list = new ArrayList<>(jsonObjectParameters);

        if (uid != null)
        {
            list.add(0, new ValuePair("__uid", String.valueOf(uid)));
        }

        setStringParam(list.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")), false);
    }

    @Override
    protected String functionName()
    {
        return "openSite";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }
}
