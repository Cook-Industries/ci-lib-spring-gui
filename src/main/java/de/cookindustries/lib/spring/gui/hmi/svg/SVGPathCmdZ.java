/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class SVGPathCmdZ extends SVGPathCommand
{

    @Override
    public String getCommandString()
    {
        return commandChar();
    }

    @Override
    protected SVGPathCommandType inferType()
    {
        return SVGPathCommandType.Z;
    }

}
