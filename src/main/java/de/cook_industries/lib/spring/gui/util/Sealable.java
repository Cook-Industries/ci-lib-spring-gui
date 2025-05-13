package de.cook_industries.lib.spring.gui.util;

import de.cook_industries.lib.spring.gui.util.exception.ObjectSealedException;

import lombok.Data;

@Data
public abstract class Sealable
{

    private final Class<?> extender;
    private Boolean        sealed = false;

    /**
     * Seal this object so it cannot be modified anymore
     */
    public final void seal()
    {
        this.sealed = true;
    }

    /**
     * Check if this element is sealed
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
