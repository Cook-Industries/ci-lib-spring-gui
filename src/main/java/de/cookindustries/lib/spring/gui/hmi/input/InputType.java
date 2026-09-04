/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum InputType
{

    /** multiple choice checkbox input */
    CHECKBOX,

    /** date input */
    DATE,

    /** file upload input */
    FILE,

    /** non visible non editable text value input */
    HIDDEN,

    /** number input */
    NUMBER,

    /** obfuscated password input */
    PASSWORD,

    /** multiple choice single select checkbox input */
    RADIO,

    /** multiple choice single select dropdown input */
    SELECT,

    /** slider input */
    SLIDER,

    /** single on/off switch input */
    SWITCH,

    /** multiline test input */
    TEXTAREA,

    /** read-only text field */
    TEXTBOX,

    /** single line text input */
    TEXTFIELD,

    /** tag input */
    TAG;

}
