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
public class SVGPathCmdH extends SVGPathCommand
{

    @NonNull
    private final Double x;

    @Override
    public String getCommandString()
    {
        return String.format(Locale.US, "%s %.3f", commandChar(), x);
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.H;
    }

}
