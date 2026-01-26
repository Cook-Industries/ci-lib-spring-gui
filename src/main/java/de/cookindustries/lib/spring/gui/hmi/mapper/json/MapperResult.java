/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private final Long                  time;

}
