package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class File extends SubmittableInput
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
