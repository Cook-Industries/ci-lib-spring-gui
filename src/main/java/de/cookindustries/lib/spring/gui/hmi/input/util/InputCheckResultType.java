/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import lombok.Getter;

/**
 * Type definition for {@link InputCheckResult}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Getter
public enum InputCheckResultType
{

    /** Indicates the check has passed */
    PASS(null, ""),

    /** Indicates the input value could not be parsed */
    NOT_PARSABLE(MarkerType.NOT_PARSABLE, "input-check-result-not-parsable"),

    /** Indicates that the input does <b>not</b> conform to a expected pattern */
    NO_PATTERN_MATCH(MarkerType.INVALID, "input-check-result-no-pattern-match"),

    /** Indicates that the input value is outside of set boundaries */
    OUT_OF_BOUNDS(MarkerType.OUT_OF_RANGE, "input-check-result-out-of-range"),

    /** Indicates that the input is effectivly {@code null} */
    NOT_PRESENT(MarkerType.NO_VALUE, "input-check-result-not-present"),

    /** Indicates that the input value is not inside a set of allowed values */
    NOT_ACCEPTED_VALUE(MarkerType.INVALID, "input-check-result-not-accpted"),

    /** Indicated that the input value is a rejected (forbidden) value */
    REJECTED_VALUE(MarkerType.INVALID, "input-check-result-not-allowed"),

    /** Indicates that the input value was expected but was empty */
    EMPTY_BUT_EXPECTED(MarkerType.INVALID, "input-check-result-expected-but-not-set"),

    /** Indicates that the input value was smaller than expected */
    TOO_SMALL(MarkerType.INVALID, "input-check-result-too-small"),

    /** Indicates that the input value was larger than expected */
    TOO_LARGE(MarkerType.INVALID, "input-check-result-too-large"),

    /** Indicates that the input values were fewer than expected */
    TOO_FEW(MarkerType.INVALID, "input-check-result-too-few"),

    /** Indicates that the input values were more than expected */
    TOO_MANY(MarkerType.INVALID, "input-check-result-too-many");

    private final MarkerType markerType;
    private final String     translationKey;

    /**
     * Create a new {@code result type} with a corresponding {@link MarkerType}.
     * 
     * @param markerType to link
     */
    InputCheckResultType(MarkerType markerType, String translationKey)
    {
        this.markerType = markerType;
        this.translationKey = translationKey;
    }

}
