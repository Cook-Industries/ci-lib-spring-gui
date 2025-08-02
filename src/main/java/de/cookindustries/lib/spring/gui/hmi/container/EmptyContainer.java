/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing at most two elements, that get put on the outer bounds of the container, depending on {@link Direction}
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class EmptyContainer extends Container
{

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.EMPTY;
    }

}
