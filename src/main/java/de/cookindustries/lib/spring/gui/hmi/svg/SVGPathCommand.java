package de.cookindustries.lib.spring.gui.hmi.svg;

import lombok.Getter;
import lombok.ToString;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public abstract class SVGPathCommand
{

    @Default
    private final boolean relative = false;

    public abstract String getCommandString();

    protected final String commandChar()
    {
        return relative
            ? inferType().name().toLowerCase()
            : inferType().name();
    }

    /**
     * Protected function to define the {@link SVGPathCommandType} of this class for the internal builder
     * 
     * @return the fix {@code type} of the definitive class implementation
     */
    protected abstract SVGPathCommandType inferType();

}
