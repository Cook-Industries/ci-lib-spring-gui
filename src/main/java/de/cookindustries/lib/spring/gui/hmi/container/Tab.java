/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import de.cookindustries.lib.spring.gui.hmi.input.util.Marker;
import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.Singular;

/**
 * A {@link Container} representing a {@code Tab}
 */
@Getter
public final class Tab
{

    @NonNull
    private final String   uid;
    @NonNull
    private final String   text;
    @Singular
    private final Marker[] errors;

    public Tab(String uid, String text, Marker... errors)
    {
        this.uid = uid;
        this.text = text;
        this.errors = errors == null ? new Marker[] {} : errors;
    }

}
