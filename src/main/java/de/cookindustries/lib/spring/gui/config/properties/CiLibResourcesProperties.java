/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * Properties for web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibResourcesProperties
{

    @NestedConfigurationProperty
    private CiLibResourcesPathProperties path = new CiLibResourcesPathProperties();

}
