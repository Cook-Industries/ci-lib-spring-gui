/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A basic container which can contain other {@link Container}s
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ContentContainer extends Container
{

    /**
     * a list of content children
     */
    @Singular
    private final List<Container> contents;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.CONTENT;
    }

}
