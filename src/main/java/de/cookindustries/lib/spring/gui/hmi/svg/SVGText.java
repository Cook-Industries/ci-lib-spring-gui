/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
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
public class SVGText extends SVGElement
{

    @NonNull
    private final String text;

    @NonNull
    @Default
    private final Double x          = 0d;

    @NonNull
    @Default
    private final Double y          = 0d;

    @NonNull
    @Default
    private final Double dx         = 0d;

    @NonNull
    @Default
    private final Double dy         = 0d;

    @NonNull
    @Default
    private final Double rotate     = 0d;

    @NonNull
    @Default
    private final Double textLength = -1d;

    @Override
    protected SVGType inferType()
    {
        return SVGType.TEXT;
    }

}
