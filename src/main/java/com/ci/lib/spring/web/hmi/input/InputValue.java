/**
 * Copyright(c) 2019 sebastian koch/CI. All rights reserved. mailto:
 * koch.sebastian@cook-industries.de
 *
 * Created on : 01.07.2019 Author : <a href="mailto:koch.sebastian@cook-industries.de">sebastian
 * koch</a>
 */
package com.ci.lib.spring.web.hmi.input;

import lombok.Data;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@Data
public class InputValue
{

    private final String text;
    private final String value;
    private Boolean      checked;
    private final String classes;

    /**
     * A simple InputValue without text
     *
     * @param value
     */
    public InputValue(String value)
    {
        this(value, "", false, "");
    }

    /**
     * A simple InputValue specified by text (e.g.: text is the visible part on a select drop down)
     *
     * @param text name of the value
     * @param value
     */
    public InputValue(String value, String text)
    {
        this(value, text, false, "");
    }

    /**
     * A simple InputValue specified by text (e.g.: text is the visible part on a select drop down)
     *
     * @param text name of the value
     * @param value
     * @param checked wether the value is selected or not
     */
    public InputValue(String value, String text, Boolean checked)
    {
        this(value, text, checked, "");
    }

    /**
     * A simple InputValue specified by text (e.g.: text is the visible part on a select drop down)
     *
     * @param text name of the value
     * @param value
     * @param classes styling classes
     */
    public InputValue(String value, String text, String classes)
    {
        this(value, text, false, classes);
    }

    /**
     * A simple InputValue specified by text (e.g.: text is the visible part on a select drop down)
     *
     * @param text name of the value
     * @param value
     * @param checked wether the value is selected or not
     * @param classes styling classes
     * 
     * @throws IllegalArgumentException if {@code value} is null
     */
    public InputValue(String value, String text, Boolean checked, String classes) throws IllegalArgumentException
    {
        if (value == null)
        {
            throw new IllegalArgumentException(String.format("value for input is null. value id: '%s'", text));
        }

        this.value = value;
        this.text = text == null ? "" : text;
        this.checked = checked != null && checked;
        this.classes = classes == null ? "" : classes;
    }
}
