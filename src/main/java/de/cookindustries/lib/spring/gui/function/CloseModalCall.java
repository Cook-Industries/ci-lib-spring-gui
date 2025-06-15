/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class CloseModalCall extends Call
{

    public CloseModalCall()
    {
        super("closeModal", 0);
    }
}
