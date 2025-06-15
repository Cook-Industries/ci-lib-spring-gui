/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.Call;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
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
