/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to ensure auto config in Spring context
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Configuration
@ComponentScan(basePackages = "de.cookindustries.lib.spring.gui")
class CiLibAutoConfig
{

}
