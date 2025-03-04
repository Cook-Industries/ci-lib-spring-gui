/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
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
