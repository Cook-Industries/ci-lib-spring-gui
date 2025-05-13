/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.response;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
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
