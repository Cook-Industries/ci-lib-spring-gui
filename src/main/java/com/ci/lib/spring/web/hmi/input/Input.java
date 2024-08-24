/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 01.07.2019 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import com.ci.lib.spring.web.hmi.ErrorMarker;
import com.ci.lib.spring.web.hmi.UiElement;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class Input extends UiElement
{

    @NonNull
    @Default
    private String                  onInput = "";
    @Singular
    private final List<ErrorMarker> errors;

    @NonNull
    private final InputType         type    = inferType();

    protected abstract InputType inferType();
}
