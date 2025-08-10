package de.cookindustries.lib.spring.gui.function;

public final class SendFromForm extends AbsFunctionCall
{

    public SendFromForm(String formId, String postUrl)
    {
        super();

        setParam(formId);
        setParam(postUrl);
    }

    @Override
    protected String functionName()
    {
        return "sendFromForm";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }
}
