package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link SVGElement} representing a {@code SVG} element
 * 
 * @since 3.5.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class SVGPath extends SVGElement
{

    @Singular
    private final List<SVGPathCommand> commands;

    @Override
    protected SVGType inferType()
    {
        return SVGType.PATH;
    }

}
