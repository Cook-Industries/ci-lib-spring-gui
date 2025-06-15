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
 * A {@link Container} representing a {@code Hidden} element
 */
@SuperBuilder
@Getter
@Jacksonized
public final class HiddenContainer extends Container
{

    @NonNull
    private final Container child;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.HIDDEN;
    }

}
