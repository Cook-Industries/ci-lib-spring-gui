package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Static base function to close the top most modal
 * 
 * @since 3.2.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoadComponent extends AbsFunctionCall
{

    public LoadComponent(String url, AbsFunctionArgs args)
    {
        super();

        setParam(url);
        setParam(args);
    }

    @Override
    protected String functionName()
    {
        return "POST";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }

}
