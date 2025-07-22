package de.cookindustries.lib.spring.gui.hmi.input.util;

import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;

/**
 * Type definition for {@link InputCheckResult}.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum InputCheckResultType {

    /** Indicates the check has passed */
    PASS(null),

    /** Indicates the input value could not be parsed */
    NOT_PARSABLE(MarkerType.NOT_PARSABLE),

    /** Indicates that the input does <b>not</b> conform to a expected pattern */
    NO_PATTERN_MATCH(MarkerType.INVALID),

    /** Indicates that the input value is outside of set boundaries */
    OUT_OF_BOUNDS(MarkerType.OUT_OF_RANGE),

    /** Indicates that the input is effectivly {@code null} */
    NOT_PRESENT(MarkerType.NO_VALUE),

    /** Indicates that the input value is not inside a set of allowed values */
    NOT_ACCEPTED_VALUE(MarkerType.INVALID),

    /** Indicated that the input value is a rejected (forbidden) value */
    REJECTED_VALUE(MarkerType.INVALID),

    /** Indicates that the input value was expected but was empty */
    EMPTY_BUT_EXPECTED(MarkerType.INVALID);

    private final MarkerType markerType;

    /**
     * Create a new {@code result type} with a corresponding {@link MarkerType}.
     * 
     * @param markerType to link
     */
    private InputCheckResultType(MarkerType markerType)
    {
        this.markerType = markerType;
    }

    /**
     * Get the corresponding {@link MarkerType} to this result type.
     * 
     * @return the corresponding {@code MarkerType}
     */
    public final MarkerType getMarkerType()
    {
        return markerType;
    }
}
