/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

/**
 * Basic text blocks to use as fallbacks
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum BasicText
{

    /** Default fallback for no text set */
    TEXT_NOT_SET("text-not-set", "TNS"),

    /** Text for 'ok' button */
    OK("ok", "ok"),

    /** Text for 'cancel' button */
    CANCEL("cancel", "cancel"),

    /** Text for 'help' button */
    HELP("help", "help"),

    /** Text for 'close' button */
    CLOSE("close", "close"),

    /** Text for 'close' button */
    NO_VALUE_SET("no.value-set", "no value set"),

    /** Text for 'close' button */
    NO_TEXT_SET("no-text-set", "no text set"),

    MANDATORY("mandatory", "mandatory"),

    INPUT_MANDATORY("input-mandatory", "input is mandatory"),

    INPUT_CHECK_RESULT_NOT_PARSABLE("input-check-result-not-parsable", "[{0}] is not parsable"),

    INPUT_CHECK_RESULT_NO_PATTERN_MATCH("input-check-result-no-pattern-match", "[{0}] does not conform to expected pattern [{1}]"),

    INPUT_CHECK_RESULT_OUT_OF_RANGE("input-check-result-out-of-range", "[{0}] is out of range [{1}]-[{2}]"),

    INPUT_CHECK_RESULT_NOT_PRESENT("input-check-result-not-present", "value is not present/null"),

    INPUT_CHECK_RESULT_NOT_ACCEPTED("input-check-result-not-accepted", "[{0}] is not accepted due to [{1}]"),

    INPUT_CHECK_RESULT_NOT_ALLOWED("input-check-result-not-allowed", "[{0}] is not allowed due to [{1}]}"),

    INPUT_CHECK_RESULT_EXPECTED_BUT_NOT_SET("input-check-result-expected-but-not-set", "value is not set but expected"),

    INPUT_CHECK_RESULT_TOO_SMALL("input-check-result-too-small", "[{0}] is too small for limit [{1}]"),

    INPUT_CHECK_RESULT_TOO_LARGE("input-check-result-too-large", "[{0}] is too large for limit [{1}]"),

    INPUT_CHECK_RESULT_TOO_FEW("input-check-result-too-few", "[{0}] has too few entries, expected min. [{1}]"),

    INPUT_CHECK_RESULT_TOO_MANY("input-check-result-too-many", "[{0}] has too many entries, expected max. [{1}]"),

    /** Shown by the browser when F5 is pressed while the loader blocks the page */
    UI_F5_BLOCKED("ui-f5-blocked",
        "F5 key is blocked to prevent reload events while waiting on a result. To enforce a reload press SHIFT + F5.",
        TranslationScope.CLIENT),

    /** Shown by the browser next to an input whose value does not match its pattern */
    UI_INPUT_PATTERN_MISMATCH("ui-input-pattern-mismatch", "value does not match the expected format [{0}]", TranslationScope.CLIENT);

    private final String           key;
    private final String           text;
    private final TranslationScope scope;

    /**
     * Basic constructor
     * 
     * @param id of this text block
     * @param text to use as a fallback
     */
    BasicText(String key, String text)
    {
        this(key, text, TranslationScope.SERVER);
    }

    /**
     * Constructor with a scope
     * 
     * @param key of this text block
     * @param text to use as a fallback
     * @param scope where this text may be used
     */
    BasicText(String key, String text, TranslationScope scope)
    {
        this.key = key;
        this.text = text;
        this.scope = scope;
    }

    /**
     * Get the fallback text assiciated with this enum
     * 
     * @return the fallback text
     */
    public final String getKey()
    {
        return key;
    }

    /**
     * Get the fallback text assiciated with this enum
     * 
     * @return the fallback text
     */
    public final String getText()
    {
        return text;
    }

    /**
     * Get the scope of this text block
     * 
     * @return where this text may be used
     */
    public final TranslationScope getScope()
    {
        return scope;
    }
}
