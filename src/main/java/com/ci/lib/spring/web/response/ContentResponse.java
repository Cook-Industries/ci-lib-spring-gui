/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.response;

import java.util.List;

import com.ci.lib.spring.web.function.Call;
import com.ci.lib.spring.web.hmi.container.Container;
import com.ci.lib.spring.web.hmi.mapper.HtmlMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public class ContentResponse extends Response
{

    public static final String LOADABLE = "loadable-content";

    @NonNull
    private final String       elementId;
    @NonNull
    @Getter(value = AccessLevel.NONE)
    private final Container    content;
    private final List<Call>   callbacks;
    private final Boolean      replace;

    @Override
    protected Action inferType()
    {
        return Action.CONTENT;
    }

    public String getContentHtml()
    {
        return HtmlMapper.map(content);
    }

}
