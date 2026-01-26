/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.Builder.Default;

/**
 * @since 3.3.2
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
@ToString
public final class WebSocketDef
{

    @NonNull
    private final String       name;

    @NonNull
    private final String       url;

    @Singular
    private final List<String> destinations;

    @NonNull
    @Default
    private final Integer      reconnectionTimeOut = 5000;

}
