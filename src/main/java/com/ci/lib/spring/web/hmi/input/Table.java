/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.input;

import java.util.List;

import com.ci.lib.spring.web.hmi.container.TableRow;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author sebastian koch <koch.sebastian@cook-industries.de>
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
