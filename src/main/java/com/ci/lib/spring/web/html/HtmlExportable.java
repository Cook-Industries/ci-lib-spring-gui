package com.ci.lib.spring.web.html;

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
