/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.modal;

import java.util.List;
import java.util.UUID;

import com.ci.lib.spring.web.hmi.UiElement;
import com.ci.lib.spring.web.hmi.container.Container;
import com.ci.lib.spring.web.hmi.container.HeadingContainer;
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
