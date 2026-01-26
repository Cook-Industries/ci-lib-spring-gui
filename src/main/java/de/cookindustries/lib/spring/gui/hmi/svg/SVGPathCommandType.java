/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Arrays;
import java.util.List;

public enum SVGPathCommandType {

    A, C, H, L, M, Q, S, T, V, Z;

    public static final List<SVGPathCommandType> ALLOWED_BASE_TYPES = Arrays.asList(SVGPathCommandType.values());

}
