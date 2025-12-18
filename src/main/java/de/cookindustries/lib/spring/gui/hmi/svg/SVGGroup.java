package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a {@code SVG} element
 * 
 * @since 3.5.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class SVGGroup extends SVGElement
{

    @Singular
    private final List<SVGElement> children;

    @Override
    protected SVGType inferType()
    {
        return SVGType.GROUP;
    }

}
