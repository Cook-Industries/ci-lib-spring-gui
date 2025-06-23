package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A placeholder dummy for a function that does effectivly nothing.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public final class VoidCall extends FunctionCall
{

    @Override
    protected String functionName()
    {
        return "";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 0;
    }

}
