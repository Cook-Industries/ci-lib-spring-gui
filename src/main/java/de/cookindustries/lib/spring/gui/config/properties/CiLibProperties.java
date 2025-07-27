package de.cookindustries.lib.spring.gui.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * General properties object for this library.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "cook-industries")
public class CiLibProperties
{

    @NestedConfigurationProperty
    private CiLibWebProperties web = new CiLibWebProperties();

}
