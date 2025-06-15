/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a {@code Link} element
 */
@SuperBuilder
@Getter
@Jacksonized
public final class LinkContainer extends Container
{

    /**
     * url to the target
     */
    @NonNull
    private final String    href;

    /**
     * target in which the link should be openend (if applicable)
     */
    @NonNull
    private final String    target;

    /**
     * content of the link
     */
    @NonNull
    private final Container content;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.LINK;
    }

}
