package de.cookindustries.lib.spring.gui.hmi.util;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;

@Builder
@Getter
@ToString
public final class WebSocketDef
{

    @NonNull
    private final String       name;

    @NonNull
    private final String       url;

    @Singular
    private final List<String> destinations;

}
