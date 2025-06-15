/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

/**
 * Interface to indicate that an object can be exported as a HTML component
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
