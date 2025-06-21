/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import de.cookindustries.lib.spring.gui.util.exception.ObjectSealedException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public abstract class Sealable
{

    @Getter(value = AccessLevel.NONE)
    private final Class<?> extender;

    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private Boolean        sealed = false;

    /**
     * Seal this object so it cannot be modified anymore
     */
    public final void seal()
    {
        this.sealed = true;
    }

    /**
     * Check if this object is sealed
     * 
     * @throws ObjectSealedException if this object is sealed
     */
    protected final void checkSeal()
    {
        if (sealed)
        {
            throw new ObjectSealedException(extender);
        }
    }
}
