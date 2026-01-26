/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ButtonIcon extends Container
{

    @NonNull
    private final String      image;

    @Default
    private final String      title    = null;

    @NonNull
    private final String      onClick;

    @NonNull
    @Default
    private final ButtonClass btnClass = ButtonClass.DEFAULT;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON_ICON;
    }

}
