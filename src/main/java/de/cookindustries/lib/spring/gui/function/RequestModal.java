package de.cookindustries.lib.spring.gui.function;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class RequestModal extends AbsFunctionCall
{

    public RequestModal(String requestUrl, List<ValuePair> jsonObjectParameters)
    {
        super();

        setStringParam(requestUrl);
        setStringParam(jsonObjectParameters.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")), false);
    }

    @Override
    protected String functionName()
    {
        return "requestModal";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }

}
