/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import java.util.List;
import java.util.Locale;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * A set settings to create html content from templates.
 * 
 * @since 3.2.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public final class ComponentSources
{

    /** the path to the template resource */
    @NonNull
    private final String                sourcePath;

    /** the language for in which the template will be resolved */
    @NonNull
    @Default
    private final Locale                locale = Locale.ENGLISH;

    /** a list of TokenMaps to store values for creation */
    @Singular
    private final List<TokenMap>        tokenMaps;

    /** a list of functions to call when the content gets loaded on the client */
    @Singular
    private final List<AbsFunctionCall> functionCalls;

}
