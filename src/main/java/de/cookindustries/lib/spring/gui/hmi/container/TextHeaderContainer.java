package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a text build up of several text parts
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class TextHeaderContainer extends Container
{

    @NonNull
    @Default
    private final Integer size = 1;

    @NonNull
    private final String  text;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.TEXT_HEADER;
    }

}
