/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
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
public final class Currency extends SubmittableInput
{

    @NonNull
    private Integer valueF;
    @NonNull
    private Integer valueB;
    @NonNull
    private String  symbol;
    @NonNull
    private Integer min;
    @NonNull
    private Integer max;
    @NonNull
    private String  placeholder;

    @Override
    protected InputType inferType()
    {
        return InputType.CURRENCY;
    }

}
