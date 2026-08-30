/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Locale;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
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

    @Default
    private final boolean angleLargeArcFlag = false;

    @Default
    private final boolean sweepFlag         = false;

    @NonNull
    private final Double  endPointX;

    @NonNull
    private final Double  endPointY;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f %.3f %s %s %.3f,%.3f", commandChar(), rx, ry,
            angleLargeArcFlag ? 1 : 0, sweepFlag ? 1 : 0, endPointX, endPointY);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.A;
    }

}
