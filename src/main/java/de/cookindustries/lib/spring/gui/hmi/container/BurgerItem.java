/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public final class BurgerItem
{

    @NonNull
    private final String  text;

    @NonNull
    private final String  icon;

    @NonNull
    @Default
    private final Integer iconSize = 2;

    @NonNull
    private final String  url;

}
