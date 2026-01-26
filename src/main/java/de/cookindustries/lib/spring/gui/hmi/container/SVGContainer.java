/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.svg.SVGElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a {@code SVG} element
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class SVGContainer extends Container
{

    private final String           xmlns = "http://www.w3.org/2000/svg";

    /**
     * {@code height} of the svg
     */
    @NonNull
    private final Integer          height;

    /**
     * {@code width} of the svg
     */
    @NonNull
    private final Integer          width;

    @Singular
    private final List<SVGElement> elements;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.SVG;
    }

}
