/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public final class BurgerItem
{

    @NonNull
    private final String text;

    @NonNull
    private final String icon;

    /**
     * icon size in {@code rem}, {@code 0} keeps the size of the stylesheet
     */
    @Default
    private final int    iconSize = 0;

    @NonNull
    private final String url;

    @NonNull
    @Default
    private final String method   = HttpMethod.POST.name();

}
