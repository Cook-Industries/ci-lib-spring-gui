/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a text build up of several text parts
 */
@SuperBuilder
@Getter
@Jacksonized
public final class TextContainer extends Container
{

    private final String text;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.TEXT;
    }

}
