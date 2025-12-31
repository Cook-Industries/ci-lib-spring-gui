package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class SVGPathCmdQ extends SVGPathCommand
{

    @NonNull
    private final Double controlPointX;

    @NonNull
    private final Double controlPointY;

    @NonNull
    private final Double endPointX;

    @NonNull
    private final Double endPointY;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f,%.3f %.3f,%.3f", commandChar(), controlPointX, controlPointY, endPointX, endPointY);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.Q;
    }

}
