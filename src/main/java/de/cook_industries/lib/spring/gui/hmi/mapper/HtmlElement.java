/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.cook_industries.lib.spring.gui.util.StringAdapter;
import de.cook_industries.lib.spring.gui.util.StringConcat;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * Object to build a HTML representation.
 */
@Builder
final class HtmlElement
{

    private static final String LT          = "<";
    private static final String LTS         = "</";
    private static final String GT          = ">";
    private static final String SPACE       = " ";
    private static final String QTM         = "\"";

    /**
     * The HTML tag of this element
     */
    @NonNull
    private String              tag;

    /**
     * A list of HTML attributes, except the 'class' attribute.
     * <p>
     * To set 'class' values use {@link HtmlElementBuilder#clazz(String)} or {@link HtmlElementBuilder#classes(java.util.Collection)}
     */
    @Singular
    private List<Attribute>     attributes;

    /**
     * A list of classes for this element
     */
    @Singular("clazz")
    private Set<String>         classes;

    /**
     * A map of HTML 'data-' attributes
     */
    @Singular
    private Map<String, String> dataAttributes;

    /**
     * Whether this element is a single tag and has no closing '</tag>'
     */
    @Default
    private Boolean             isSingleTag = false;

    /**
     * A list of {@code String}s between the opening and closing tag. They get appened one after another
     * <p>
     * Is only used if {@link HtmlElementBuilder#isSingleTag(Boolean)} is {@code true}.
     */
    @Singular
    private List<String>        contents;

    /**
     * Generate a HTML string from this object
     * 
     * @return a HTML string of this object
     */
    public String html()
    {
        StringConcat sc = new StringConcat();

        sc
                .append(StringAdapter.withPrefixAndSuffix(LT, tag, SPACE))
                .append(getAttributes())
                .append(SPACE)
                .append(StringAdapter.withPrefixAndSuffix("class=\"", StringAdapter.withSeparatorFrom(classes, SPACE), QTM))
                .append(SPACE)
                .append(getDataAttributes())
                .append(GT)
                .append(!isSingleTag, () -> StringAdapter.from(contents))
                .append(!isSingleTag, () -> StringAdapter.withPrefixAndSuffix(LTS, tag, GT));

        return sc.getString();
    }

    /**
     * Get the attributes as a {@code String}
     * 
     * @return a {@code String} representation of the attributes
     */
    private String getAttributes()
    {
        return attributes.stream().map(e -> e.getHtmlRep()).collect(Collectors.joining(SPACE));
    }

    /**
     * Get the data-attributes as a {@code String}
     * 
     * @return a {@code String} representation of the data-attributes
     */
    private String getDataAttributes()
    {
        return dataAttributes
                .entrySet()
                .stream()
                .map(e -> StringAdapter.withPrefixAndSuffix("data-", e.getKey(), "=\"") + StringAdapter.withSuffix(e.getValue(), QTM))
                .collect(Collectors.joining(SPACE));
    }

}
