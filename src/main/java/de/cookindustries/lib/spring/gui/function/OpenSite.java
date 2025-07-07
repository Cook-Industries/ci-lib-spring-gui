package de.cookindustries.lib.spring.gui.function;

public class OpenSite<T> extends AbsFunctionCall
{

    public OpenSite(String requestUrl, AbsFunctionArgs args)
    {
        super();

        setParam(requestUrl);
        setParam(args);
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
