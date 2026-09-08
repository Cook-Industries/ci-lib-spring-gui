/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a playable {@code Audio} element
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class AudioContainer extends Container
{

    /**
     * {@code source path} to the resource
     */
    @NonNull
    private final String  src;

    @NonNull
    private final String  caption;

    /**
     * whether if controls should be shown or not
     */
    @Default
    private final boolean controls = true;

    /**
     * whether this audio should start playing automatically
     */
    @Default
    private final boolean autoplay = false;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.AUDIO;
    }

}
