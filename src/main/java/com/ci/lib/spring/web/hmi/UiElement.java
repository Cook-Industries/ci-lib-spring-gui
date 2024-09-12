/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.micrometer.common.lang.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Singular;

@SuperBuilder
@Getter
public class UiElement
{

    @NonNull
    @Default
    private String              uid = UUID.randomUUID().toString();
    @Singular("clazz")
    private Set<String>         classes;
    @Singular
    private Map<String, String> dataAttributes;

}
