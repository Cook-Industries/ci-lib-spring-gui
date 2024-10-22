/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.response;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CompoundResponse extends Response
{

    @Singular
    private List<Response> responses;

    @Override
    protected Action inferType()
    {
        return Action.COMPOUND;
    }

}
