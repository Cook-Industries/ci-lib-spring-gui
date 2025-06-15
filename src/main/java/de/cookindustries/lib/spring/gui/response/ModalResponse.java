/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.modal.Modal;
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
