/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.file;

/**
 * Holder class for static paths to folders or files.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class FileAndFolderPaths
{

    public static final String EXTERNAL_FOLDER_STATIC_CSS   = "/css";
    public static final String EXTERNAL_FOLDER_STATIC_JS    = "/js";
    public static final String EXTERNAL_FOLDER_STATIC_IMAGE = "/image";

    public static final String USER_FILE_CONFIG_CSS         = "config/css-config.json";
    public static final String TEMPLATE_DEFAULT_PATH        = "templates/";

    private FileAndFolderPaths()
    {
        // prevent instantiation
    }
}
