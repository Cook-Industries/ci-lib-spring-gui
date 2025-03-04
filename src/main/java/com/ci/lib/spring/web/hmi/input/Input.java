/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import com.ci.lib.spring.web.hmi.UiElement;
import com.ci.lib.spring.web.hmi.input.util.Marker;

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
    private String             onInput = "";
    @Singular("marker")
    private final List<Marker> marker;

    @NonNull
    private final InputType    type    = inferType();

    protected abstract InputType inferType();
}
