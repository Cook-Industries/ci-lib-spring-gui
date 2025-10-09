/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.util.JsProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @since 3.3.2
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@ToString
public final class UiPropertiesResponse extends Response
{

    @NonNull
    private final JsProperties properties;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.PROPERTIES;
    }

}
