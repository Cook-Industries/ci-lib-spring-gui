/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;
import de.cookindustries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class ModalResponse extends Response
{

    @NonNull
    private ModalContainer modal;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.MODAL;
    }

    public String getContentHtml()
    {
        return ContainerHtmlMapper.map(modal);
    }
}
