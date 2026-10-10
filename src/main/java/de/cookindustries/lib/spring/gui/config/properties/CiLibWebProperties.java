/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * Properties for web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibWebProperties
{

    @Valid
    @NestedConfigurationProperty
    private CiLibWebPathProperties   path   = new CiLibWebPathProperties();

    @Valid
    @NestedConfigurationProperty
    private CiLibWebUploadProperties upload = new CiLibWebUploadProperties();

}
