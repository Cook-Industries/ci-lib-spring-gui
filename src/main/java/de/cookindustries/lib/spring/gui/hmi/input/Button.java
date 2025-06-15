/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Button extends Input
{

    @NonNull
    private String      text;
    @NonNull
    private String      onClick;
    @Default
    private ButtonClass btnClass = ButtonClass.DEFAULT;

    @Override
    protected InputType inferType()
    {
        return InputType.BUTTON;
    }

}
