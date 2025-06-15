/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a playable {@code Audio} element
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

    /**
     * whether if controls should be shown or not
     */
    @NonNull
    private final Boolean controls;

    /**
     * whether this audio should start playing outmatically
     */
    @NonNull
    private final Boolean autoplay;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.AUDIO;
    }

}
