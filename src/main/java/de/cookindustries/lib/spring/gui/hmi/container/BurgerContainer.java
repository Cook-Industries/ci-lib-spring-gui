package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a group of several {@link Button}s or {@link ButtonIcon}s
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class BurgerContainer extends Container
{

    @NonNull
    @Default
    private final String           image = "/images/burger-menu-icon.svg";

    @Singular
    private final List<BurgerItem> items;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BURGER;
    }

}
