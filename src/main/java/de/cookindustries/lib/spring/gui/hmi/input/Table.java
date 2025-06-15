/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input;

import java.util.List;

import de.cookindustries.lib.spring.gui.hmi.container.TableRow;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 */
@SuperBuilder
@Getter
@Jacksonized
public final class Table extends Input
{

    private final Boolean        editable;
    private final List<String>   colNames;
    private final List<TableRow> rows;

    @Override
    protected InputType inferType()
    {
        return InputType.TABLE;
    }

}
