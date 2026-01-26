/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response.message;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum MessageType {

    SUCCESS("bi-check-circle"), ERROR("bi-x-circle"), WARNING("bi-exclamation-circle"), NOTIFICATION("bi-question-circle");

    private final String icon;

    private MessageType(String icon)
    {
        this.icon = icon;
    }

    public String getIcon()
    {
        return icon;
    }

}
