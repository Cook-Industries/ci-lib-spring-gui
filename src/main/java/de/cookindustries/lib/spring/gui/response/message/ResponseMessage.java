/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class ResponseMessage
{

    @NonNull
    private final MessageTarget target = inferTarget();

    @NonNull
    private final MessageType   type;

    public final String getIcon()
    {
        return type.getIcon();
    }

    protected abstract MessageTarget inferTarget();

}
