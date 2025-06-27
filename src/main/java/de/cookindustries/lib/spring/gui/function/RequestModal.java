package de.cookindustries.lib.spring.gui.function;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class RequestModal extends AbsFunctionCall
{

    public RequestModal(String requestUrl, List<ValuePair> parameters)
    {

        super();

        setStringParam(requestUrl);
        setStringParam(parameters.stream().map(p -> p.exportInJsonNotation()).collect(Collectors.joining(", ", "{", "}")));
    }

    @Override
    protected String functionName()
    {
        return "";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }

}
