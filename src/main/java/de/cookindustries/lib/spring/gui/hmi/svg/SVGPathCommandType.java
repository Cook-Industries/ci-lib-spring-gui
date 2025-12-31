package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Arrays;
import java.util.List;

public enum SVGPathCommandType {

    A, C, H, L, M, Q, S, T, V, Z;

    public static final List<SVGPathCommandType> ALLOWED_BASE_TYPES = Arrays.asList(SVGPathCommandType.values());

}
