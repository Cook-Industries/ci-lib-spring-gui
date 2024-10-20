/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.container;

import com.ci.lib.spring.web.hmi.input.Marker;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.Singular;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@Getter
public class Tab
{

    @NonNull
    private final String   uid;
    @NonNull
    private final String   text;
    @Singular
    private final Marker[] errors;

    public Tab(String uid, String text, Marker... errors)
    {
        this.uid = uid;
        this.text = text;
        this.errors = errors == null ? new Marker[] {} : errors;
    }

}
