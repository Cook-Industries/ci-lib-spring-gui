/**
 * Copyright(c) 2020 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 14.02.2020 Author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import java.util.List;

import com.ci.lib.spring.web.hmi.input.Input;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
 */
@SuperBuilder
@Getter
public class FormContainer extends Container
{

    @Singular
    private List<Input> inputs;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.FORM;
    }

}
