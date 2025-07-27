package de.cookindustries.lib.spring.gui.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * Properties for web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibWebProperties
{

    @NestedConfigurationProperty
    private CiLibWebPathProperties path = new CiLibWebPathProperties();

}
