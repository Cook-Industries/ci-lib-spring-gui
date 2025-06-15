/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.input.Button;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a group of several {@link ButtonContainer} or {@link ButtonIconContainer}
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
    private List<Button> buttons;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON_BAR;
    }

}
