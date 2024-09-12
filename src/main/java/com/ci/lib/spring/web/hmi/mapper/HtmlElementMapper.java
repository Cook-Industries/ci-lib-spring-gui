/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ci.lib.spring.web.util.StringAdapter;
import com.ci.lib.spring.web.util.StringConcat;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

@Builder
final class HtmlElementMapper
{

    private static final String LT          = "<";
    private static final String LTS         = "</";
    private static final String GT          = ">";
    private static final String SPACE       = " ";
    private static final String QTM         = "\"";

    @NonNull
    private String              tag;

    @Singular
    private List<Attribute>     attributes;

    @Singular("clazz")
    private Set<String>         classes;

    @Singular
    private Map<String, String> dataAttribues;

    @Default
    private Boolean             isSingleTag = false;

    @NonNull
    @Default
    private String              content     = "";

    public String html()
    {
        StringConcat sc = new StringConcat();

        sc
                .append(StringAdapter.withPrefixAndSuffix(LT, tag, SPACE))
                .append(getAttributes())
                .append(SPACE)
                .append(StringAdapter.withPrefixAndSuffix("class=\"", StringAdapter.withSeparatorFrom(classes, SPACE), QTM))
                .append(!classes.isEmpty(), SPACE)
                .append(getDataAttributes())
                .append(GT)
                .append(!isSingleTag, content)
                .append(!isSingleTag, StringAdapter.withPrefixAndSuffix(LTS, tag, GT));

        return sc.getString();
    }

    private String getAttributes()
    {
        return attributes.stream().map(e -> e.html()).collect(Collectors.joining(SPACE));
    }

    private String getDataAttributes()
    {
        return dataAttribues
                .entrySet()
                .stream()
                .map(e -> StringAdapter.withPrefixAndSuffix("data-", e.getKey(), "=\"") + StringAdapter.withSuffix(e.getValue(), QTM))
                .collect(Collectors.joining(SPACE));
    }

}
