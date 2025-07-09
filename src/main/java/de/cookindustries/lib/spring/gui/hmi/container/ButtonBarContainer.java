/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a group of several {@link Button}s or {@link ButtonIcon}s
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ButtonBarContainer extends Container
{

    /**
     * list of {@code buttons} in this bar
     */
    @Singular
    private final List<Button> buttons;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON_BAR;
    }

}
