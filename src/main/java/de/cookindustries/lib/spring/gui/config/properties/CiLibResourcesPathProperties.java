/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import de.cookindustries.lib.spring.gui.config.file.FileAndFolderPaths;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Path properties in relation to web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibResourcesPathProperties
{

    /** A path for template content in the resources folder */
    @NotBlank(message = "template-resources path must not be blank")
    private String templates = FileAndFolderPaths.TEMPLATE_DEFAULT_PATH;

}
