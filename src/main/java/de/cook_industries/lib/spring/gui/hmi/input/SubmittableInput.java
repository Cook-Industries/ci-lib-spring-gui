/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class SubmittableInput extends Input
{

    @NonNull
    private String name;
    
    @NonNull
    private String submitAs;
}
