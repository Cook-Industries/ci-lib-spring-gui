/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 */
@SuperBuilder
@Getter
@Jacksonized
public final class ButtonIcon extends Input
{

    @NonNull
    private String      image;
    @NonNull
    private String      onClick;
    @NonNull
    private ButtonClass btnClass;

    @Override
    protected InputType inferType()
    {
        return InputType.BUTTON_ICON;
    }

}
