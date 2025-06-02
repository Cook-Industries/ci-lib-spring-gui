/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.response;

import java.util.List;

import de.cook_industries.lib.spring.gui.function.Call;
import de.cook_industries.lib.spring.gui.hmi.container.Container;
import de.cook_industries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class ContentResponse extends Response
{

    public static final String LOADABLE  = "loadable-content";

    /**
     * Element id to place content in
     */
    @NonNull
    @Default
    private final String       elementId = LOADABLE;

    /**
     * {@link Container} to be sent
     */
    @NonNull
    @Getter(value = AccessLevel.NONE)
    private final Container    content;
    /**
     * {@link Call}s to perform
     */
    private final List<Call>   callbacks;

    /**
     * Whether or not this content should be replace the old content or be appended to it
     */
    @NonNull
    @Default
    private final Boolean      replace   = false;

    @Override
    protected Action inferType()
    {
        return Action.CONTENT;
    }

    public String getContentHtml()
    {
        return ContainerHtmlMapper.map(content);
    }

}
