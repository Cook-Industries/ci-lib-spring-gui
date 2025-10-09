/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * Settings to submit for a {@link RegisterTagInput} function.
 * 
 * @since 3.2.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public class TagInputSettings extends AbsFunctionArgs
{

    @NonNull
    private final String  id;

    @NonNull
    private final Boolean enforceWhitelist;

    @NonNull
    @Default
    private final String  fetchWhitelistUrl = "";

    @NonNull
    @Default
    private final String  searchTagsUrl     = "";

    @NonNull
    @Default
    private final Integer maxTags           = Integer.MAX_VALUE;

}
