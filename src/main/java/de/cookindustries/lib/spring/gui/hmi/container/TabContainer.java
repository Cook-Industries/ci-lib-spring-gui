/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.Set;

import io.micrometer.common.lang.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@link Container} representing a holder for several {@link Tab}s
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class TabContainer extends Container
{

    @Singular
    private Set<Tab>       tabs;
    @Singular
    private Set<Container> bodies;
    @NonNull
    @Default
    private Boolean        disabled = Boolean.FALSE;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.TAB;
    }

}
