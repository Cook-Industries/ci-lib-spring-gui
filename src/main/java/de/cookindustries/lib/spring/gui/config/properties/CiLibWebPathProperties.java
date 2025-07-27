package de.cookindustries.lib.spring.gui.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Path properties in relation to web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibWebPathProperties
{

    /** A path for static content on the system */
    @NotBlank(message = "static-web-resources path must not be blank")
    private String staticWebResources = "./resources/";

    /** A path where the server can place temporary up-/download data */
    @NotBlank(message = "temp-upload path must not be blank")
    private String tempUpload         = "./temp/";

}
