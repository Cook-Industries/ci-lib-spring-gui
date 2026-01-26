/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

/**
 * Interface to indicate that an object can be exported as a HTML component
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public interface HtmlExportable
{

    /**
     * Get a HTML representation of this object
     * 
     * @return a HTML String of this object
     */
    public String getHtmlRep();
}
