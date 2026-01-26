/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;
import java.util.stream.Collectors;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.html.HtmlMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
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
@ToString
public final class ContentResponse extends Response
{

    public static final String    LOADABLE  = "loadable-content";

    /**
     * Element id to place content in
     */
    @NonNull
    @Default
    private final String          elementId = LOADABLE;

    /**
     * {@link Container}s to be sent
     */
    @Singular
    private final List<Container> contents;

    /**
     * How this content should interact with existing content
     */
    @NonNull
    @Default
    private final ContentHandling handling  = ContentHandling.APPEND;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.CONTENT;
    }

    public String getContentHtml()
    {
        return HtmlMapper
            .map(contents)
            .stream()
            .collect(Collectors.joining("\n"));
    }

}
