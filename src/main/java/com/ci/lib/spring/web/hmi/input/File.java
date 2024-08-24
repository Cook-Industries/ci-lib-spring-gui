package com.ci.lib.spring.web.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class File extends SubmittableInput
{

    @NonNull
    private String value;

    @Override
    protected InputType inferType()
    {
        return InputType.DATE;
    }

}
