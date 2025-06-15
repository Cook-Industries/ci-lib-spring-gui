/**
 * 
 */
package de.cookindustries.lib.spring.gui.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to ensure auto config in Spring context
 */
@Configuration
@ComponentScan(basePackages = "de.cookindustries.lib.spring.gui")
public class AutoConfig
{

}
