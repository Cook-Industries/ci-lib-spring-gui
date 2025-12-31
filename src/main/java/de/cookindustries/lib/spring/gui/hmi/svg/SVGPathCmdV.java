package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class SVGPathCmdV extends SVGPathCommand
{

    @NonNull
    private final Double y;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f", commandChar(), y);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.V;
    }

}
