/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class SVGPathCmdC extends SVGPathCommand
{

    @NonNull
    private final Double controlPoint1X;

    @NonNull
    private final Double controlPoint1Y;

    @NonNull
    private final Double controlPoint2X;

    @NonNull
    private final Double controlPoint2Y;

    @NonNull
    private final Double endPointX;

    @NonNull
    private final Double endPointY;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f,%.3f %.3f,%.3f %.3f,%.3f", commandChar(), controlPoint1X, controlPoint1Y,
            controlPoint2X, controlPoint2Y, endPointX, endPointY);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.C;
    }

}
