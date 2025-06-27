package de.cookindustries.lib.spring.gui.function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpenSiteCall extends AbsFunctionCall
{

    public OpenSiteCall(String requestUrl, List<ValuePair> parameters)
    {
        this(requestUrl, null, parameters);
    }

    public OpenSiteCall(String requestUrl, Integer uid, List<ValuePair> parameters)
    {

        super();

        setStringParam(requestUrl);

        List<ValuePair> list = new ArrayList<>(parameters);

        if (uid != null)
        {
            list.add(0, new ValuePair("__uid", uid));
        }

        setStringParam(parameters.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")));
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
