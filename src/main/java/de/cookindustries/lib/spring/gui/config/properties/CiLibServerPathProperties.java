/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Path properties in relation to server folders.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibServerPathProperties
{

    /** A path for static content on the system */
    @NotBlank(message = "static-web-resources path must not be blank")
    private String staticWebResources = "./resources/";

}
