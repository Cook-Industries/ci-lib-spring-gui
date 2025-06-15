/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 */
@SuperBuilder
@Getter
@Jacksonized
public final class File extends SubmittableInput
{

    @Default
    private Boolean      multiple = false;

    @Singular
    private List<String> accepts;

    @Override
    protected InputType inferType()
    {
        return InputType.FILE;
    }

}
