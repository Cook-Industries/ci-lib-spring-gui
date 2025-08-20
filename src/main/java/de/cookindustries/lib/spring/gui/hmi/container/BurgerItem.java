package de.cookindustries.lib.spring.gui.hmi.container;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public final class BurgerItem
{

    @NonNull
    private final String text;

    @NonNull
    private final String image;

    @NonNull
    private final String url;

}
