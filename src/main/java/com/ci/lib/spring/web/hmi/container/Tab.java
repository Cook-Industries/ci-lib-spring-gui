/**
 * Copyright(c) 2021 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 25.01.2021 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.hmi.container;

import com.ci.lib.spring.web.hmi.ErrorMarker;

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
    private final String        uid;
    @NonNull
    private final String        text;
    @Singular
    private final ErrorMarker[] errors;

    public Tab(String uid, String text, ErrorMarker... errors)
    {
        this.uid = uid;
        this.text = text;
        this.errors = errors == null ? new ErrorMarker[] {} : errors;
    }

}
