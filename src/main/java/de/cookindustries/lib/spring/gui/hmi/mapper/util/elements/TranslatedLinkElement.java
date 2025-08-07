/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util.elements;

import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class TranslatedLinkElement extends FlatMappable
{

    @NonNull
    private final String text;

    @NonNull
    private final String href;

}
