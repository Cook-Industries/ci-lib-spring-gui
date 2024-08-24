/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 01.07.2019 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.hmi.modal;

import java.util.List;
import java.util.UUID;

import com.ci.lib.spring.web.hmi.UiElement;
import com.ci.lib.spring.web.hmi.container.Container;
import com.ci.lib.spring.web.hmi.mapper.HtmlMapper;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class Modal extends UiElement
{

    @NonNull
    private String          name;
    @NonNull
    private String          requestUrl;
    @NonNull
    @Default
    private String          identifier          = UUID.randomUUID().toString();
    @NonNull
    @Singular
    private List<Container> containers;
    @NonNull
    @Default
    private Boolean         closeOnOverlayClick = false;

    public String getContentHtml()
    {
        return HtmlMapper.map(containers);
    }
}
