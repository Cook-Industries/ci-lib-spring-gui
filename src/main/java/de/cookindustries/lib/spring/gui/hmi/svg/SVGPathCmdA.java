package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class SVGPathCmdA extends SVGPathCommand
{

    @NonNull
    private final Double  rx;

    @NonNull
    private final Double  ry;

    @NonNull
    @Default
    private final Integer angleLargeArcFlag = 0;

    @NonNull
    @Default
    private final Integer sweepFlag         = 0;

    @NonNull
    private final Double  endPointX;

    @NonNull
    private final Double  endPointY;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f %.3f %s %s %.3f,%.3f", commandChar(), rx, ry,
            angleLargeArcFlag <= 0 ? 0 : 1, sweepFlag <= 0 ? 0 : 1, endPointX, endPointY);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.A;
    }

}
