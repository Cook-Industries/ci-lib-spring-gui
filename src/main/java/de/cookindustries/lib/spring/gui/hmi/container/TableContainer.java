/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 3.0.2
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public final class TableContainer extends Container
{

    @NonNull
    private final String          name;

    @Singular
    private final List<String>    columnNames;

    @NonNull
    @Default
    private final Boolean         sortable = false;

    @Singular
    private final List<Container> rows;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.TABLE;
    }

}
