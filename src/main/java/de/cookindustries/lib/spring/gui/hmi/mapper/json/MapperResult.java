package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public final class MapperResult
{

    @Singular
    private final List<Container>       containers;

    @Singular
    private final List<AbsFunctionCall> functions;

}
