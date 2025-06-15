/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import de.cookindustries.lib.spring.gui.hmi.input.Button;
import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a textual {@code Button}
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ButtonContainer extends Container
{

    /**
     * the underlying button input
     */
    @NonNull
    private Button button;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON;
    }

}
