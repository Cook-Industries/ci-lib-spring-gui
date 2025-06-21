/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import de.cookindustries.lib.spring.gui.hmi.input.Button;
import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a textual {@code Button}
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ButtonContainer extends Container
{

    /**
     * the underlying {@code button}
     */
    @NonNull
    private Button button;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON;
    }

}
