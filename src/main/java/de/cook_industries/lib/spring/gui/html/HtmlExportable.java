/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.html;

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
