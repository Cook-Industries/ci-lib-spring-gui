/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 01.07.2019 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public class InputValue
{

    @NonNull
    private final String       text;
    @NonNull
    private final String       value;
    @NonNull
    private Boolean            checked;

    @Singular("clazz")
    private final List<String> classes;

}
