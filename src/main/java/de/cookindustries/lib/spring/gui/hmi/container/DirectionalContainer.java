package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * Container type that can have a direction
 * 
 * @since 4.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class DirectionalContainer extends Container
{

    /**
     * the direction of this {@code Container}, if applicable
     */
    @NonNull
    @Default
    private final Direction direction = Direction.NONE;

}
