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

/**
 * A simple object holding a translated link element {@link String}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class TranslatedLinkElement extends FlatMappable
{

    @NonNull
    private final String text;

    @NonNull
    private final String href;

}
