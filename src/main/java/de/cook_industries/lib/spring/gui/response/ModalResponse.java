/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.response;

import de.cook_industries.lib.spring.gui.hmi.modal.Modal;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
public class ModalResponse extends Response
{

    @NonNull
    private Modal modal;

    @Override
    protected Action inferType()
    {
        return Action.MODAL;
    }
}
