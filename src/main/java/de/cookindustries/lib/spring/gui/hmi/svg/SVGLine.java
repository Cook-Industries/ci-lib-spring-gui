/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import lombok.Getter;
import lombok.NonNull;
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
public class SVGLine extends SVGElement
{

    /**
     * {@code x coordinate} of the first point
     */
    @NonNull
    private final Double x1;

    /**
     * {@code x coordinate} of the second point
     */
    @NonNull
    private final Double x2;

    /**
     * {@code y coordinate} of the first point
     */
    @NonNull
    private final Double y1;

    /**
     * {@code y coordinate} of the second point
     */
    @NonNull
    private final Double y2;

    @Override
    protected SVGType inferType()
    {
        return SVGType.LINE;
    }

}
