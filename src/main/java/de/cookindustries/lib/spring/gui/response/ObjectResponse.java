/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @since 3.6.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@ToString
public final class ObjectResponse<T> extends Response
{

    @NonNull
    private final String function;

    @NonNull
    private final T      object;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.OBJECT;
    }

}
