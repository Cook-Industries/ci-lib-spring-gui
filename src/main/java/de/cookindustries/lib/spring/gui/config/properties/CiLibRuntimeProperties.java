/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Properties for web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibRuntimeProperties
{

    @Min(10)
    private int dissectionUnhealthyRecursionDepth = 25;

}
